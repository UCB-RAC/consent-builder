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

package edu.berkeley.rac.ophs.consentBuilder.ui.home

import scala.collection.JavaConversions.mapAsJavaMap

import com.github.peholmst.mvp4vaadin.navigation.ControllablePresenter
import com.github.peholmst.mvp4vaadin.navigation.ControllableView
import com.github.peholmst.mvp4vaadin.navigation.Direction
import com.github.peholmst.mvp4vaadin.navigation.ViewController

import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import edu.berkeley.rac.ophs.consentBuilder.ui.forms.UserFormView
import edu.berkeley.rac.ophs.consentBuilder.ui.util.DocumentDownloader
import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication
import javax.persistence.Entity


class HomePresenter(view: HomeView, application: ConsentBuilderApplication)
  extends ControllablePresenter[HomeView](view)
  with DocumentDownloader {

    def createNewConsent()
    {
    	val consent = new Consent()
    	consent.creator = application.getUser.asInstanceOf[String]
    	editConsentFromStart(consent)
    }
    
    def editConsentFromStart(consent: Consent)
    {
      val params = Map(UserFormView.KEY_CONSENT -> consent,
          UserFormView.KEY_CONTINUE -> Boolean.box(false) //boxing Scala Boolean to match Java type signature of goToView() below
          )
      getViewController.goToView(UserFormView.VIEW_ID, params)
    }
    
    def continueConsent(consent: Consent)
    {
      val params = Map(UserFormView.KEY_CONSENT -> consent,
          UserFormView.KEY_CONTINUE -> Boolean.box(true) //boxing Scala Boolean to match Java type signature of goToView() below
          )
      getViewController.goToView(UserFormView.VIEW_ID, params)
    }

	protected override def viewShown(viewController: ViewController,
			userData: java.util.Map[String, Object], oldView: ControllableView,
			direction: Direction)
	{
		updateTaskList()
	}

	protected def updateTaskList()
	{
		val currentUser = application.getUser.asInstanceOf[String]
		getView.setConsents(application.getConsentDao.getUserConsents(currentUser))
	}
	
	def exportDocx(consent: Consent)
	{
	  exportDocx(application, consent)
	}
}
