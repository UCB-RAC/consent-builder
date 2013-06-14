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

import com.vaadin.ui.Field
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import org.springframework.expression.Expression
import org.springframework.expression.spel.standard.SpelExpressionParser

import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import edu.berkeley.rac.ophs.consentBuilder.model.ConsentAnswer

import javax.persistence.Transient


class SingleQuestionForm(field: Field, title: String, explanation: String, answer: String)
	extends VerticalLayout with ConsentForm {

	{
		setSpacing(true)
	}
	
	@Transient
	val spelParser = new SpelExpressionParser()
	var spelExpression = "textAnswers['" + answer + "']"
	var propertyExpression = spelParser.parseExpression(spelExpression)
  
	def setSpelExpression(property: String)
	{ 
	  spelExpression = property
	  propertyExpression = spelParser.parseExpression(spelExpression)
	}
	def getSpelExpression = spelExpression
  
	override def getID = formKey
	override def getTitle = title
	override def getExplanation = explanation
	override def setRelevance(update: java.util.Map[String, String]) = super.setRelevance(update)
  
	var defaultText: String = _
	def setDefaultText (text: String): Unit = { defaultText = text }
	override def getDefaultText: String = defaultText
	
	def load(consent: Consent): Unit = {

		addComponent(new Label(explanation, Label.CONTENT_XHTML))

		val savedValue = propertyExpression.getValue(consent)
		field.setValue(
		    savedValue match
		    {
		      case null => defaultText
		      case _ => savedValue toString
		    }
		    )
		addComponent(field)
		
	}
	
	override final def save(consent: Consent) { save(consent, getAnswer) }
	
	final def save(consent: Consent, answer: ConsentAnswer)
	{
	  answer setTimestamp((java.util.Calendar getInstance) getTime)
	  propertyExpression setValue(consent, answer)
	}
	
	def getAnswer: ConsentAnswer =
	  field getValue match
	  {
	    case null =>
	      {
	        val answer = new ConsentAnswer
	        answer setValue null
	        answer
	      }
	    case text =>
	      {
	        val answer: ConsentAnswer = text toString;
	        answer
	      }
	  }
	

}