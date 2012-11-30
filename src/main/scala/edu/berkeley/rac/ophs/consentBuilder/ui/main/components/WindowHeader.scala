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

package edu.berkeley.rac.ophs.consentBuilder.ui.main.components

import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window.Notification
import com.vaadin.ui.themes.Reindeer
import edu.berkeley.rac.scala.vaadin.Conversions._
import edu.berkeley.rac.ophs.consentBuilder.ui.main.MainPresenter


class WindowHeader(mainPresenter: MainPresenter) extends HorizontalLayout {

	var currentUserLabel: Label = _
	var logoutButton: Button = _
	var numberOfUnassignedTasks = -1
	var numberOfMyTasks = -1
	
	{
		setWidth("100%")
		setMargin(true)
		setSpacing(true)
		addStyleName(Reindeer.LAYOUT_BLACK)

		val title = createTitle()
		addComponent(title)
		setComponentAlignment(title, Alignment.MIDDLE_LEFT)
		setExpandRatio(title, 1.0F)

		currentUserLabel = new Label("foo")
//		currentUserLabel.setWidth("60ex")
		currentUserLabel.setSizeUndefined()
		addComponent(currentUserLabel)
		setComponentAlignment(currentUserLabel, Alignment.MIDDLE_RIGHT)

		logoutButton = createLogoutButton()
		addComponent(logoutButton)
		setComponentAlignment(logoutButton, Alignment.MIDDLE_RIGHT)
	}

	def createTitle() = {
		val appTitle = new Label("UC Berkeley Consent Builder")
		appTitle.addStyleName(Reindeer.LABEL_H1)
		appTitle
	}

	def createLogoutButton() = {
		val button = new Button("Logout")
		button.addListener(() => mainPresenter.logout())
		button.addStyleName(Reindeer.BUTTON_SMALL)
		button
	}

	def setNameOfCurrentUser(username: String) {
		currentUserLabel.setContentMode(Label.CONTENT_XHTML)
		currentUserLabel.setValue("<b>%s</b>".format(username))
	}
}
