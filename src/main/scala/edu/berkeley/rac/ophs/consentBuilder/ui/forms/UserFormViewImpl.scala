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

import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.BaseTheme
import org.springframework.context.ApplicationContext

import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication
import edu.berkeley.rac.ophs.consentBuilder.ui.util.AbstractConsentBuilderView
import edu.berkeley.rac.scala.vaadin.Conversions._


class UserFormViewImpl(application: ConsentBuilderApplication)
	extends	AbstractConsentBuilderView[UserFormView, UserFormPresenter]
    with UserFormView {

  var layoutWidth = "72ex"
  var submitButton: Button = _
  var skipButton: Button = _
  var backButton: Button = _

  var formContainerLayout: VerticalLayout = _

  var currentForm: ConsentForm = _

  {
    init()
  }

  protected override def initView() {
    super.initView()
    getViewComponent.addStyleName("user-form-view")

    formContainerLayout = new VerticalLayout
    //		formContainerLayout.setSizeFull()
    formContainerLayout setWidth layoutWidth
    formContainerLayout addStyleName "form-container"
    getViewLayout addComponent formContainerLayout
    getViewLayout setExpandRatio (formContainerLayout, 1.0F)

    submitButton = new Button("Save and Continue")
    submitButton addListener (() => if (currentForm != null) getPresenter.submitForm(currentForm))
    submitButton setVisible false
    getViewComponent addComponent submitButton

    val buttonBar = new HorizontalLayout()
    buttonBar setSpacing true
    buttonBar setWidth "100%"

    backButton = new Button("Back to previous question")
    backButton setStyleName BaseTheme.BUTTON_LINK
    backButton addListener (() => if (currentForm != null) getPresenter previousForm currentForm)
    backButton setVisible false
    buttonBar addComponent backButton

    skipButton = new Button("Skip to next question")
    skipButton setStyleName BaseTheme.BUTTON_LINK
    skipButton addListener (() => if (currentForm != null) getPresenter.nextForm(currentForm))
    skipButton setVisible false
    buttonBar addComponent skipButton
//    buttonBar setComponentAlignment (skipButton, Alignment.MIDDLE_RIGHT)

    getViewComponent addComponent buttonBar
  }

  override def getDisplayName =
    if (currentForm != null) currentForm.getTitle else "No form available"

  override def getDescription =
    if (currentForm != null) currentForm.getExplanation else "There is no form to show"

  protected override def createPresenter() = new UserFormPresenter(application, this)

  override def setForm(form: ConsentForm) {
    currentForm = form
    updateControls()
  }

  override def hideForm() {
    currentForm = null
    updateControls()
  }

//  private def navbuttonText(direction: String, formName: String): String =
//    {
//      formName match {
//        case null => direction
//        case _ => direction + " to " + formName
//      }
//    }

  private def updateControls() {
    updateHeaderLabel()
    submitButton setVisible (currentForm != null)
    // skipButton setCaption (navbuttonText("Skip", (getPresenter nextFormTitle currentForm)))
    skipButton setVisible (currentForm != null)
    // backButton setCaption (navbuttonText("Back", ((getPresenter previousFormTitle currentForm))))
    backButton setVisible (currentForm != null)
    formContainerLayout.removeAllComponents()
    if (currentForm != null) {
      formContainerLayout.addComponent(currentForm)
    }
  }
}
