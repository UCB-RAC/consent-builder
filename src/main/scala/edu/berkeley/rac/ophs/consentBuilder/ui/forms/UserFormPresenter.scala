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

import com.github.peholmst.mvp4vaadin.navigation.ControllablePresenter
import com.github.peholmst.mvp4vaadin.navigation.ControllableView
import com.github.peholmst.mvp4vaadin.navigation.Direction
import com.github.peholmst.mvp4vaadin.navigation.ViewController

import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import edu.berkeley.rac.ophs.consentBuilder.ui.util.DocumentDownloader
import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication
import javax.persistence.Entity


class UserFormPresenter(application: ConsentBuilderApplication, view: UserFormView)
	extends ControllablePresenter[UserFormView](view)
	with DocumentDownloader 
	{

	var consent: Consent = _
	var formKey: String = _
	// val startForm: String = application.getFormContext.getAliases("startForm")(0)
	val formList: FormList = application.getFormContext.getBean("formList").asInstanceOf[FormList]
	val startForm: String = (formList first) match { case Some(form) => form getID; case None => null }
	
	protected override def viewShown(viewController: ViewController, userData: java.util.Map[String, Object],
	                                 oldView: ControllableView, direction: Direction) {
	    if (userData == null) throw new NullPointerException()

		consent = userData.get(UserFormView.KEY_CONSENT).asInstanceOf[Consent]
		userData.get(UserFormView.KEY_CONTINUE).asInstanceOf[Boolean] match
		{
	      case true => formKey = getFormToContinue
//	        formList nextRelevant (formList getFormByID (consent getLastAnswered), consent) match
//	        {
//	          case None =>
//	            consent getFirstSkipped match
//	            {
//	              case null => startForm
//	              case f => f
//	            }
//	          case Some(form) => form getID 
//	        }
	      case _ => formKey = startForm
	    }
	    showTaskForm()
		
	}

	def submitForm(form: ConsentForm) {
		
	    form.save(consent)
	    application.getConsentDao.save(consent)
	    consent setLastAnswered formKey
	    if (consent.getFirstSkipped == formKey) consent.setFirstSkipped(null) //clear the first-skipped bookmark if submitting at the bookmark
	    //dan: the use of null for setting and testing in this logic seems like unidiomatic Scala, but using '_' didn't work as expected.
	    nextForm(form)
	}
	
	def nextFormKey(form: ConsentForm): String =
	{
	  formList.nextRelevant(form, consent) match
	  {
	    case None => return "(none)"
	    case Some(f) => return f getID
	  }
	}
	
	def nextFormTitle(form: ConsentForm): String =
	{
	  formList nextRelevant (form, consent) match
	  {
	    case None => return null
	    case Some(f) => return f getTitle
	  }
	}

	def previousFormTitle(form: ConsentForm): String =
	{
	  formList previousRelevant (form, consent) match
	  {
	    case None => return null
	    case Some(f) => return f getTitle
	  }
	}

	def nextForm(form: ConsentForm)
	{
	  formList nextRelevant (form, consent) match
	  {
	    case Some(f) => {
	      formKey = f getID;
	      showTaskForm()
	    }
	    case None =>
	    {
	      println("End of wizard")
	      doEndOfList()
//	      val answers = consent getTextAnswers;
//	      answers get("generateOnFinish") match
//	      {
//	        case null => {}
//	        case answer => 
//	        {
//	          println ("generateOnFinish: " + answer)
//	          if (answer contains "Yes") exportDocx (application, consent)
//	          answers.remove("generateOnFinish")
//	        }
//	      }
	      getViewController goBack
	    }
	  }
	}
	
	def previousForm(form: ConsentForm)
	{
	  formList previousRelevant (form, consent) match
	  {
	    case Some(f) =>
	    {
	      formKey = f getID;
	      showTaskForm()
	    }
	    case None => getViewController goBack
	  }
	}

	private def showTaskForm() {
	  
	    if (formKey == null)
	    {
	    	formKey = startForm
	    }
		val form = application.getFormContext.getBean(formKey).asInstanceOf[ConsentForm]
		form.load(consent)
		if (consent.getFirstSkipped == null) consent.setFirstSkipped(formKey) //set the "resume here" bookmark if none exists
		//dan: the use of null for setting and testing in this logic seems like unidiomatic Scala, but using '_' didn't work as I expected.
		getView.setForm(form)

	}
	
	private def getFormToContinue() =
	{
	  formList nextRelevant (formList getFormByID (consent getLastAnswered), consent) match
	    {
	      case None =>
	        consent getFirstSkipped match
	        {
	          case null => startForm
	          case f => f
	        }
	      case Some(form) => form getID
	    }
	}
	
	private def doEndOfList()
	{
	      val answers = consent getTextAnswers;
	      answers get("generateOnFinish") match
	      {
	        case null => {}
	        case answer => 
	        {
	          println ("generateOnFinish: " + answer)
	          if (answer contains "Yes") exportDocx (application, consent)
	          answers.remove("generateOnFinish")
	        }
	      getViewController goBack
	      }
	}
}
