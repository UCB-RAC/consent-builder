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

package edu.berkeley.rac.ophs.consentBuilder.ui.main

import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.VerticalLayout
import com.github.peholmst.mvp4vaadin.AbstractView
import com.github.peholmst.mvp4vaadin.VaadinView
import com.github.peholmst.mvp4vaadin.navigation.ControllableView
import com.github.peholmst.mvp4vaadin.navigation.ViewProvider
import com.github.peholmst.mvp4vaadin.navigation.ui.NavigationBar
import com.github.peholmst.mvp4vaadin.navigation.ui.ViewContainerComponent
import edu.berkeley.rac.ophs.consentBuilder.ui.main.components.WindowHeader
import edu.berkeley.rac.ophs.consentBuilder.ui.home.HomeViewImpl
import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication


class MainViewImpl(application: ConsentBuilderApplication, viewProvider: ViewProvider)
  extends AbstractView[MainView, MainPresenter]
  with MainView
  with VaadinView
{

	init()

	var viewLayout: VerticalLayout = _
    var windowHeader: WindowHeader = _
    var navigationBar: NavigationBar = _
    var viewContainer: ViewContainerComponent = _

    override def getDisplayName = "UCB Consent Builder"

    override def getDescription = ""

	override def createPresenter = new MainPresenter(this, application, viewProvider)

	protected override def initView() = {
		viewLayout = new VerticalLayout()
		viewLayout.addStyleName("main-view")
		//viewLayout.setSizeFull() "full" (100%x100%) sizing prevents scrolling.

		windowHeader = createWindowHeader()
		viewLayout.addComponent(windowHeader)

		navigationBar = createNavigationBar()
		viewLayout.addComponent(navigationBar)

		viewContainer = createViewContainer()
		viewLayout.addComponent(viewContainer)
		viewLayout.setExpandRatio(viewContainer, 1.0F)

		createAndAddHomeView()
	}

	def createWindowHeader() = new WindowHeader(getPresenter)

	def createNavigationBar() = {
      val navigationBar = new NavigationBar() {
		override def addBreadcrumbForView(view: ControllableView) {
		  // hack; only show first item on breadcrumb trail
          if (getViewController().getTrail().size() <= 1) {
            super.addBreadcrumbForView(view)
          }
		}
	  }
	  navigationBar.setViewController(getPresenter.getViewController)
	  navigationBar.addStyleName("breadcrumbs")
	  navigationBar.setWidth("100%")
	  navigationBar.setMargin(true)
	  navigationBar
	}

	def createViewContainer() = {
		val viewContainer = new ViewContainerComponent()
		viewContainer.setMargin(true)
		viewContainer.addStyleName("main-view-viewcontainer")
		viewContainer.setViewController(getPresenter.getViewController)
		//viewContainer.setSizeFull()
		viewContainer.setSizeUndefined()
		viewContainer.setWidth("100%")
		viewContainer
	}

	def createAndAddHomeView() = {
		val homeView = new HomeViewImpl(viewProvider, application)
		getPresenter.getViewController.goToView(homeView)
	}

	override def getViewComponent = viewLayout

	override def setNameOfCurrentUser(username: String) {
		windowHeader.setNameOfCurrentUser(username)
	}

}
