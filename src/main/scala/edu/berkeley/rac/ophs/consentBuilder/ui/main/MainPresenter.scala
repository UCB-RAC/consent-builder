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

import com.github.peholmst.mvp4vaadin.Presenter
import com.github.peholmst.mvp4vaadin.navigation.DefaultViewController
import com.github.peholmst.mvp4vaadin.navigation.ViewController
import com.github.peholmst.mvp4vaadin.navigation.ViewProvider

import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication


class MainPresenter(view: MainView, application: ConsentBuilderApplication, viewProvider: ViewProvider) extends Presenter[MainView](view) {

	val viewController = new DefaultViewController
	viewController.setViewProvider(viewProvider)

	override def init() {
		val currentUser = getNameOfCurrentUser
		getView.setNameOfCurrentUser(currentUser)
	}

	def getViewController = viewController

	def logout() {
		fireViewEvent(new UserLoggedOutEvent(view))
	}

	def getNameOfCurrentUser = {
		val currentUserId = application.getUser().asInstanceOf[String]
		val userInfo = application.getDirectoryService.getUserInfo(currentUserId)
		String.format("%s %s", userInfo._1, userInfo._2)
	}

}
