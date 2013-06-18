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

import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.ContextMapper
import org.springframework.ldap.core.DirContextAdapter


class CalNetService(ldapTemplate: LdapTemplate) {
  
  def NO_AUTH = "!unauthenticated"
  val NO_AUTH_DISPLAY = ("No", "User")
  val calNetPeopleSearchBase = "ou=people,dc=berkeley,dc=edu"
  val calNetGuestsSearchBase = "ou=guests,dc=berkeley,dc=edu"
  val searchBases = List(calNetPeopleSearchBase, calNetGuestsSearchBase)
  val ctxNameMapper: ContextMapper = new ContextMapper(){
    override def mapFromContext(ctx: Any): (String, String) =
      ctx match 
      {
        case context: DirContextAdapter => 
          (context getStringAttribute "givenName", context getStringAttribute "sn")
        case _ =>
          throw new RuntimeException(
              "Cannot handle LDAP search result object of type " + ctx.getClass)
      }
  }
  
  def getUserInfo(uid: String): (String /*first name*/, String /*last name*/) =

    if (uid == NO_AUTH)
      NO_AUTH_DISPLAY
    else
    {
      val filter = "(uid=%s)".format(uid)
      searchBases.foldLeft[(String, String)](("", "")) {
        (acc, base) =>
          {
            (try 
              Some( ldapTemplate.searchForObject(base, filter, ctxNameMapper) )
            catch 
              { case erdae: org.springframework.dao.EmptyResultDataAccessException => None }
            ) match
            {
              case Some(name) => name.asInstanceOf[(String, String)]
              case None => acc
            }
          }
      }
    }
  
}