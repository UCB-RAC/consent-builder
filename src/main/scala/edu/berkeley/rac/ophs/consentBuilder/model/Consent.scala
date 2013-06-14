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

import scala.reflect.BeanProperty
import scala.collection.JavaConversions._
import javax.persistence.CollectionTable
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.FetchType.EAGER

object Consent {
  val DATE_FORMAT = java.text.DateFormat getDateInstance
}

@Entity
class Consent {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var creator: String = _

  @BeanProperty
  var firstSkipped: String = _

  @BeanProperty
  var lastAnswered: String = _
  
//  @BeanProperty
//  var answersVersion: Int = _
  
//  @BeanProperty
//  var firstMigrationSkipped: String = _
  
  @ElementCollection(fetch = EAGER)
  @CollectionTable(
    name = "CONSENT_TEXT_ANSWERS"
    , joinColumns = Array(new JoinColumn(name = "CONSENT_ID"))
    )
  //@MapKeyColumn(name = "TEXTANSWERS_KEY", insertable = false, updatable = false)
  @BeanProperty
  var textAnswers: java.util.Map[String, ConsentAnswer] = new java.util.HashMap[String, ConsentAnswer]
  
  def setDisplayTitle: Unit = {}
  def getDisplayTitle: String =
    (getAnswerText("studyTitle") match
    {
      case None => "(no title)"
      case Some(title) => title
    }) + (getAnswerText("studySubtitle") match
    {
      case None => ""
      case Some(subtitle) => ": " + subtitle
    })
  
  def getDisplayDate: String =
    getDateModified match
    {
      case null => ""
      case date => Consent.DATE_FORMAT format date
    }
    
  def getDateModified: java.util.Date =
    ((textAnswers values).foldLeft[Option[java.util.Date]](None)(
        (acc, curr) => (acc, Option(curr getTimestamp)) match
        {
          case (None, None) => None
          case (None, Some(date)) => Some(date)
          case (Some(maxdate), None) => Some(maxdate)
          case (Some(maxdate), Some(date)) => Option(List(maxdate, date) max)
        }
        )) match
        {
          case None => null
          case Some(date) => date
        }
  
  def getAnswerText(key: String): Option[String] =
    textAnswers get key match
    {
      case null => None
      case answer => (answer toString) trim match
      {
        case "" => None
        case trimmed => Option(trimmed)
      }
    }
  
  def getAnswerTimestamp(key: String): Option[java.util.Date] =
    textAnswers get key match
    {
      case null => None
      case answer => Option(answer getTimestamp)
    }
  
}
