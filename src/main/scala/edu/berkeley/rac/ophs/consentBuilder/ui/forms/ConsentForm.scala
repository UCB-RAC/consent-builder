/*
 * Copyright ©2012 The Regents of the University of California (Regents).
 * All Rights Reserved. Permission to use, copy, modify, and
 * distribute this software and its documentation for educational,
 * research, and not-for-profit purposes, without fee and without a
 * signed licensing agreement, is hereby granted, provided that the
 * above copyright notice, this paragraph and the following two
 * paragraphs appear in all copies, modifications, and distributions.
 * Contact The Office of Technology Licensing, UC Berkeley,
 * 2150 Shattuck Avenue, Suite 510, Berkeley, CA 94720-1620,
 * (510) 643-7201, for commercial licensing opportunities.
 * 
 * IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT,
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE
 * AND ITS DOCUMENTATION, EVEN IF REGENTS HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING
 * DOCUMENTATION, IF ANY, PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * REGENTS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

package edu.berkeley.rac.ophs.consentBuilder.ui.forms

import scala.annotation.implicitNotFound
import scala.collection.JavaConversions.mapAsScalaMap
import org.springframework.beans.factory.BeanNameAware
import org.springframework.expression.spel.standard.SpelExpressionParser
import com.vaadin.ui.Component
import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import javax.persistence.Entity
import edu.berkeley.rac.ophs.consentBuilder.model.ConsentAnswer
import scala.beans.BeanProperty


trait ConsentForm extends Component with BeanNameAware
{

    def getID: String
    def getTitle: String
    def getExplanation: String
    def getDefaultText: String
    
    var formKey: String = _
    override def setBeanName(name: String) {formKey = name}
    
    @BeanProperty
    var asOfTimestamp: java.util.Date = _
    
    var beanRelevance: java.util.Map[String, String] = _
    val parser = new SpelExpressionParser
    var relevanceTest: (Consent) => Boolean = _
    
    def getRelevance = beanRelevance
	def setRelevance(update: java.util.Map[String, String])
    {
      beanRelevance = update
      val relevanceMap = update toMap
      
      if (relevanceMap == null || (relevanceMap isEmpty))
      {
        relevanceTest = (consent: Consent) => true
      }
      else if ("SpelExpression" equalsIgnoreCase ((relevanceMap head) _1))
      {
        val relevanceExpression = parser parseExpression ((relevanceMap head) _2)
        relevanceTest =
          (consent: Consent) => 
            if (consent == null) 
            {
              true
            }
            else
            {
              relevanceExpression getValue(consent, classOf[Boolean])  
            }
      }
      else
      {
        relevanceTest =
          (consent: Consent) => 
            relevanceMap forall (
              {
                case (key: String, value: String) =>
                {
                  consent getAnswerText key match
                  {
                    case None => false
                    case Some(str) => str contains value
                  }
                }
              }
              )
      }
      
    }
    
	def isRelevant(consent: Consent): Boolean = (relevanceTest == null) || relevanceTest(consent)
	
	def isCurrent(consent: Consent): Boolean = 
	  Option(asOfTimestamp) match
	  {
	    case None => true
	    case Some(timestamp) =>
	      consent getAnswerTimestamp formKey match
	      {
	        case None => false
	        case Some(savedTime) => (savedTime compareTo timestamp) >= 0
	      }
	  }
		
	/*
	 * load the consent model into the UI form
	 */
	def load(consent: Consent)

	/*
	 * save the UI form into the consent model
	 */
	def save(consent: Consent)
	
	override def equals(a: Any): Boolean =
	  a.isInstanceOf[ConsentForm] &&
	  a.asInstanceOf[ConsentForm].getID == this.getID &&
	  (Option(a.asInstanceOf[ConsentForm].getAsOfTimestamp) match
	  {
	    case Some(otherTimestamp) =>
	      Option(this.getAsOfTimestamp) match
	      {
	        case Some(timestamp) => otherTimestamp.equals(timestamp)
	        case None => false
	      }
	    case None =>
	      Option(this.getAsOfTimestamp) match
	      {
	        case Some(timestamp) => false
	        case None => true
	      }
	  })
	  
	
}
