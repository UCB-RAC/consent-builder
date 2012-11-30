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

import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import edu.berkeley.rac.ophs.consentBuilder.model.ConsentAnswer
import scala.collection.JavaConversions._
import java.util.List


class FormList(springItems: java.util.List[ConsentForm])  //using the java type to allow constructor call from Spring XML 
{
  private val forward: scala.collection.immutable.List[ConsentForm] = (springItems toList) distinct
  private val backward = forward reverse
  
  def nextRelevant(form: ConsentForm, consent: Consent): Option[ConsentForm] =
    forward dropWhile (scan => scan != form) match
    {
      case Nil => None
      case forms => (forms tail) find (f => f isRelevant consent)
    }
    
  def previousRelevant(form: ConsentForm, consent: Consent): Option[ConsentForm] =
    backward dropWhile (scan => scan != form) match
    {
      case Nil => None
      case forms => (forms tail) find (f => f isRelevant consent)
    }
    
  def first: Option[ConsentForm] =
    forward head match
    {
      case null => None
      case _ => Some (forward head)
    }
  
  def getFormByID(id: String): ConsentForm =
    if (id == null)
      null
    else
      forward find (f => id equals (f getID)) match
      {
        case None => null
        case Some(formID) => formID
      }
  
  def getMigrationList(consent: Consent) =
    new FormList(forward filterNot (_ isCurrent consent) filter (_ isRelevant consent))
  
  def isCurrent(consent: Consent) =
    getMigrationList(consent) forward match
    {
      case Nil => true
      case _ => false
    }
    
}