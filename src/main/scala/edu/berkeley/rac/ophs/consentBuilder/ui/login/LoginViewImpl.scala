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

package edu.berkeley.rac.ophs.consentBuilder.ui.login

import com.github.peholmst.mvp4vaadin.AbstractView
import com.github.peholmst.mvp4vaadin.VaadinView
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Link
import com.vaadin.ui.Window.Notification
import com.vaadin.ui.Alignment
import com.github.peholmst.mvp4vaadin.View
import com.vaadin.ui.ComponentContainer
import com.vaadin.terminal.ExternalResource


class LoginViewImpl(calNetBaseUrl: String, serviceUrl: String) extends AbstractView[View, LoginPresenter](true)
  with VaadinView {
  
    private var viewLayout: VerticalLayout = _

	override def getDisplayName = "UCB Consent Builder Login"

	override def getDescription = ""

	override def createPresenter = new LoginPresenter(this)

	override def getViewComponent = viewLayout

	override def initView = {
	  
	    val titlePanel = new VerticalLayout
	    titlePanel.setSpacing(true)
	    titlePanel.setWidth("640px")
	    
	    val title = new Label("Consent Builder")
    	title.addStyleName(Reindeer.LABEL_H1)
    	titlePanel.addComponent(title)
    	
    	val subtitle = new Label("UC Berkeley’s online tool for creating Word document consent forms")
    	titlePanel.addComponent(subtitle)


    	val loginPanel = new VerticalLayout
    	loginPanel.setSpacing(true)
    	loginPanel.setWidth("320px")
    	
    	val header = new Label("Please log in")
    	header.addStyleName(Reindeer.LABEL_H2)
    	loginPanel.addComponent(header)

    	val loginLink =
    		new Link("Authenticate via CalNet",
    		         new ExternalResource(calNetBaseUrl + "login?service=" + serviceUrl + "&renew=true"))
    	loginPanel.addComponent(loginLink)
    	
    	
    	val footerPanel = new HorizontalLayout()
	    footerPanel.setSpacing(true)
	    
	    val instructionsLink =
	      new Link("Consent Builder Instructions",
	          new ExternalResource("http://cphs.berkeley.edu/consentbuilder.html"))
	    footerPanel.addComponent(instructionsLink)
	    footerPanel.setComponentAlignment(instructionsLink, Alignment.MIDDLE_LEFT)
	    val ophsLink =
	      new Link("CPHS/OPHS Home Page",
	          new ExternalResource("http://cphs.berkeley.edu/"))
	    footerPanel.addComponent(ophsLink)
	    footerPanel.setComponentAlignment(ophsLink, Alignment.MIDDLE_RIGHT)
	    footerPanel.setSpacing(true)
	    footerPanel.setWidth("640px")
	    
    	val centerLayout = new VerticalLayout()
	    centerLayout.addComponent(titlePanel)
	    centerLayout.setComponentAlignment(titlePanel, Alignment.TOP_LEFT)
    	centerLayout.addComponent(loginPanel)
    	centerLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER)
    	centerLayout.addComponent(footerPanel)
    	centerLayout.setComponentAlignment(footerPanel, Alignment.BOTTOM_LEFT)
    	centerLayout.setSpacing(true)
    	centerLayout.setHeight("60%")
    	centerLayout.setWidth("640px")
    	
    	viewLayout = new VerticalLayout()
	    viewLayout.addComponent(centerLayout)
	    viewLayout.setComponentAlignment(centerLayout, Alignment.MIDDLE_CENTER)
    	//viewLayout.setMargin(true)
    	viewLayout.setSizeFull()
    	viewLayout.addStyleName(Reindeer.LAYOUT_BLACK)
	}

}