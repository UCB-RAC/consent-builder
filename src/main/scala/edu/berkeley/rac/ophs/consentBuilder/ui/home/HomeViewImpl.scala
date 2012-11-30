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

import scala.collection.JavaConversions._
import scala.collection.mutable.Buffer

import com.github.peholmst.mvp4vaadin.VaadinView
import com.github.peholmst.mvp4vaadin.navigation.AbstractControllableView
import com.github.peholmst.mvp4vaadin.navigation.ControllableView
import com.github.peholmst.mvp4vaadin.navigation.ViewProvider
import com.vaadin.data.util.BeanItemContainer
import com.vaadin.event.LayoutEvents.LayoutClickEvent
import com.vaadin.event.LayoutEvents.LayoutClickListener
import com.vaadin.terminal.Sizeable
import com.vaadin.terminal.ThemeResource
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.CssLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Table
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout

import edu.berkeley.rac.scala.vaadin.Conversions._
import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication
import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import edu.berkeley.rac.ophs.consentBuilder.ui.forms.UserFormView


class HomeViewImpl(viewProvider: ViewProvider, application: ConsentBuilderApplication)
	extends AbstractControllableView[HomeView, HomePresenter] with HomeView with VaadinView {

    {
      println("invoking HomeViewImpl init method")
      init()
    }
  
    var viewLayout: VerticalLayout = _
    var viewProvider: ViewProvider = _
    var dataSource: BeanItemContainer[Consent] = _

    override def getDisplayName = "Home"

    override def getDescription = "The starting point of Vaadin Activiti Demo"

    protected override def createPresenter() = new HomePresenter(this, application)

	override def getViewComponent = viewLayout

	protected override def initView()
    {
		viewLayout = new VerticalLayout()
		viewLayout.addStyleName("home-view")
		viewLayout.setMargin(true)
		viewLayout.setSpacing(true)

		val introLayout = new CssLayout()
		introLayout.setWidth("100%")
		
		val newConsentButton = new Button("Create a New Consent")
		newConsentButton.addListener(() => getPresenter.createNewConsent())
		introLayout.addComponent(newConsentButton)
		
		val header = new Label("or click on an existing consent below:")
		header.setWidth(Sizeable.SIZE_UNDEFINED, 0)
		header.addStyleName(Reindeer.LABEL_H2)
		introLayout.addComponent(header)
		
		viewLayout.addComponent(introLayout)

		val consentTable = new Table()
		dataSource = new BeanItemContainer[Consent](classOf[Consent])
		consentTable.setContainerDataSource(dataSource)
//		consentTable.setColumnHeader("creator", "Author")
		consentTable.setColumnHeader("displayTitle", "Title")
		consentTable.setColumnHeader("dateModified", "Last Modified")
		consentTable.setVisibleColumns(Array(/*"creator", "displayTitle", */"dateModified"))
		consentTable.setWidth("100%")
		consentTable.setSelectable(true)
		consentTable.setImmediate(true)
		consentTable.setNullSelectionAllowed(false) // to disable deselect when clicking selected consent
		consentTable.setColumnReorderingAllowed(true)
				
//		viewLayout.addComponent(consentTable)
		
		val buttonBar = new HorizontalLayout()
		buttonBar.setSpacing(true)
		
		val editButtonGroup = new HorizontalLayout()
		editButtonGroup.setSpacing(true)
		
		val fromBeginningButton = new Button("Edit From Beginning")
		fromBeginningButton.setEnabled(consentTable.getValue() != null)
		consentTable.addListener(() => fromBeginningButton.setEnabled(consentTable.getValue() != null))
		fromBeginningButton.addListener(() => getPresenter.editConsentFromStart(consentTable.getValue().asInstanceOf[Consent]))
		editButtonGroup.addComponent(fromBeginningButton)
		
		val continueButton = new Button("Continue Editing")
		continueButton.setDescription("Edit consent form starting at the last question you answered")
		continueButton.setEnabled(consentTable.getValue() != null)
		consentTable.addListener(() => continueButton.setEnabled(consentTable.getValue() != null))
		continueButton.addListener(() => getPresenter.continueConsent(consentTable.getValue().asInstanceOf[Consent]))
		editButtonGroup.addComponent(continueButton)
		
		val outputButtonGroup = new HorizontalLayout()
		outputButtonGroup.setSpacing(true)
		
		val exportButton = new Button ("Create Word Document")
		exportButton.setIcon(new ThemeResource("icons/word.gif"))
		exportButton.setDescription("Generate the selected consent form as a Microsoft Word document")
		exportButton.setEnabled(consentTable.getValue() != null)
		consentTable.addListener(() => exportButton.setEnabled(consentTable.getValue() != null))
		exportButton.addListener(() => getPresenter.exportDocx(consentTable.getValue().asInstanceOf[Consent]))
		outputButtonGroup.addComponent(exportButton)

		buttonBar.addComponent(editButtonGroup)
		buttonBar.addComponent(outputButtonGroup)
		
		viewLayout.addComponent(buttonBar)
	}

    override def setConsents(consents: Buffer[Consent]) {
		dataSource.removeAllItems()
		dataSource.addAll(consents)
	}

}
