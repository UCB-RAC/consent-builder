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

package edu.berkeley.rac.ophs.consentBuilder.model

import java.lang.CharSequence
import javax.persistence.Embeddable
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob
import scala.reflect.BeanProperty
import javax.persistence.Temporal
import javax.persistence.TemporalType


@Embeddable
class ConsentAnswer extends CharSequence
{
  @BeanProperty
  @Lob
  var value: String = _
  
  @BeanProperty
  @Temporal(TemporalType.TIMESTAMP)
  var timestamp: java.util.Date = _
  
  override def toString = value
  
  def subSequence(a: Int, b: Int) = value subSequence(a, b)
  def charAt(a: Int) = value charAt(a)
  def length = value length
  
}

object ConsentAnswer
{
  implicit def unwrap(lob: ConsentAnswer): String = if (lob != null) lob value else null
  
  implicit def wrap(stringValue: String): ConsentAnswer =
    {
      val wrappedString = new ConsentAnswer
      wrappedString value = stringValue
      wrappedString timestamp = (java.util.Calendar getInstance) getTime
      
      wrappedString
    }
}
