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
import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import com.vaadin.ui.Label
import scala.collection.JavaConversions._
import edu.berkeley.rac.ophs.consentBuilder.model.ConsentAnswer
import scala.collection.immutable.HashSet


class MultiSelectForm(field: Field, title: String, explanation: String, property: String)
	extends SingleQuestionForm(field: Field, title: String, explanation: String, property: String)
{ 
  val defaultSelections: java.util.List[String] = (
    defaultText match
    {
      case null => "" 
      case _ => defaultText 
    }
    ) split "," toList
    
  override def load(consent: Consent)
  {
    addComponent(new Label(explanation, Label.CONTENT_XHTML))

    val savedValue = propertyExpression.getValue(consent)
    var loadValues: java.util.List[String] = savedValue match
    {
      case null => defaultSelections
      case _ => (savedValue toString) split "," toList
    }
    field setValue loadValues
    println("Loading \"" + loadValues + "\" from " + property)

	addComponent(field)
  }
  
  override def getAnswer =
  {
	/*
	 * @TODO I suspect that there is an error coming into saving multiselect answers due to not checking
	 *   for null field values; however, the Vaadin multiselect field may behave differently from the
	 *   built-in text field and always have a non-null Set<String> representation -- need to verify.
	 */
    
    val answer: ConsentAnswer = (field getValue).asInstanceOf[java.util.Set[String]] mkString(",")
	println("Saving \"" + answer + "\" to " + property)
	answer
  }

}