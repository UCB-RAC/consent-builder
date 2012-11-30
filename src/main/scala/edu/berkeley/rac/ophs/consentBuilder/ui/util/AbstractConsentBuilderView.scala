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

package edu.berkeley.rac.ophs.consentBuilder.ui.util

import com.github.peholmst.mvp4vaadin.VaadinView
import com.github.peholmst.mvp4vaadin.navigation.AbstractControllableView
import com.github.peholmst.mvp4vaadin.navigation.ControllablePresenter
import com.github.peholmst.mvp4vaadin.navigation.ControllableView
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.Reindeer


abstract class AbstractConsentBuilderView[V <: ControllableView, P <: ControllablePresenter[V]](init: Boolean = false)
		extends AbstractControllableView[V, P] with VaadinView {

	private var viewLayout: VerticalLayout = _

	private var header: Label = _

	override def getViewComponent = viewLayout

	protected final def getViewLayout = viewLayout

	override protected def initView() {
		viewLayout = new VerticalLayout()
//		viewLayout.setSizeFull()
		viewLayout.setSizeUndefined()
		viewLayout.setMargin(true)
		viewLayout.setSpacing(true)

		val headerLayout = new HorizontalLayout()
		headerLayout.setWidth("100%")
		headerLayout.setSpacing(true)
		viewLayout.addComponent(headerLayout)

		header = new Label(getDisplayName())
		header.addStyleName(Reindeer.LABEL_H1)
		headerLayout.addComponent(header)
		headerLayout.setComponentAlignment(header, Alignment.MIDDLE_LEFT)
		headerLayout.setExpandRatio(header, 1.0F)

		addAdditionalControlsToHeader(headerLayout)

//		val backButton = new Button("« Go Back")
//		backButton.addStyleName(Reindeer.BUTTON_SMALL)
//		backButton.addListener(new Button.ClickListener() {
//
//			override def buttonClick(event: Button#ClickEvent) {
//				getViewController.goBack()
//			}
//		})
//		headerLayout.addComponent(backButton)
//		headerLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT)
	}

	protected def updateHeaderLabel() {
		// In case the display name is dynamic
		header.setValue(getDisplayName)
	}

	protected def addAdditionalControlsToHeader(headerLayout: HorizontalLayout) {

	}
}
