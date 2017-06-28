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

package edu.berkeley.rac.ophs.consentBuilder.service

import scala.collection.JavaConversions._
import org.springframework.orm.hibernate3.support.HibernateDaoSupport
import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import scala.collection.mutable.Buffer


class ConsentDao extends HibernateDaoSupport {

	def save(consent: Consent) {
	    getHibernateTemplate.saveOrUpdate(consent)
	}
	
	def getUserConsents(uid: String): Buffer[Consent] = {
		val query = "from edu.berkeley.rac.ophs.consentBuilder.model.Consent where creator = ? order by id desc"
		getHibernateTemplate.find(query, uid).asInstanceOf[java.util.List[Consent]]
	}
  
}
