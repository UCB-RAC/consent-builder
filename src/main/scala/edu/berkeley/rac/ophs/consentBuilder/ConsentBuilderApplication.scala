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

package edu.berkeley.rac.ophs.consentBuilder

import scala.collection.JavaConversions._
import java.net.URL
import com.vaadin.Application
import com.vaadin.ui.Window
import com.vaadin.terminal.DownloadStream
import com.vaadin.terminal.ParameterHandler
import com.vaadin.terminal.URIHandler
import com.vaadin.terminal.gwt.server.WebApplicationContext
import org.jasig.cas.client.authentication.AttributePrincipalImpl
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.context.support.GenericXmlApplicationContext
import com.github.peholmst.mvp4vaadin.ViewListener
import com.github.peholmst.mvp4vaadin.navigation.DefaultViewProvider
import com.github.peholmst.mvp4vaadin.ViewEvent
import edu.berkeley.rac.ophs.consentBuilder.ui.login.LoginViewImpl
import edu.berkeley.rac.ophs.consentBuilder.ui.forms.UserFormView
import edu.berkeley.rac.ophs.consentBuilder.ui.forms.UserFormViewImpl
import edu.berkeley.rac.ophs.consentBuilder.ui.main.MainViewImpl
import edu.berkeley.rac.ophs.consentBuilder.service.LdapDirectoryService
import edu.berkeley.rac.ophs.consentBuilder.ui.main.UserLoggedOutEvent
import edu.berkeley.rac.ophs.consentBuilder.service.ConsentDao
import scala.beans.BeanProperty


class ConsentBuilderApplication extends Application with ViewListener {

	private var loginView: LoginViewImpl = _
	private var mainView: MainViewImpl = _
	private var viewProvider: DefaultViewProvider = _
	private var casTicketValidator: Cas20ServiceTicketValidator = _
	private var applicationContext: ApplicationContext = _
	private val formContext = new GenericXmlApplicationContext
	private var consentDao: ConsentDao = _
	private var casBaseUrl: String = _
	
	private var authentication: Boolean = _
	
	def getFormContext = formContext
	
	override def init = {
	  
	    authentication = System getProperty ("authentication") match { case "false" => false ; case "off" => false; case _ => true }
	    
		setTheme("ucb-consent-builder");
	
	  	val servletContext = getContext.asInstanceOf[WebApplicationContext].getHttpSession.getServletContext
		applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
	
		consentDao = applicationContext.getBean("consentDao").asInstanceOf[ConsentDao]
		
		formContext.load(applicationContext.getBean("formConfigResource").asInstanceOf[Resource])
	  	formContext.setParent(applicationContext)
	  	formContext.refresh()
	  	
	  	if (authentication)
	  	{
	  	  casBaseUrl = applicationContext.getBean("casBaseUrl").asInstanceOf[String]
	  	  casTicketValidator = new Cas20ServiceTicketValidator(casBaseUrl)
	  	  createAndShowLoginWindow(casBaseUrl)
	  	}
	  	else
	  	{
	  	  setUser(getDirectoryService.NO_AUTH)
	  	  createAndShowMainWindow
	  	}
		
	}
  
	private def createAndShowLoginWindow(calNetBaseUrl: String) = {
		val service = new URL(getURL, "login").toString
		loginView = new LoginViewImpl(calNetBaseUrl, service)
		loginView.addListener(this)
		val loginWindow = new Window(loginView.getDisplayName, loginView.getViewComponent)
		var ticket: String = null
    
		loginWindow.addParameterHandler(new ParameterHandler {
			override def handleParameters(parameters: java.util.Map[String, Array[String]]) {
				if (parameters.containsKey("ticket")) ticket = parameters.get("ticket")(0)
			}
		})
		loginWindow.addURIHandler(new URIHandler {
			override def handleURI(context: URL, relativeUri: String): DownloadStream = {
				if ("login".equals(relativeUri)) {
					val assertion = casTicketValidator.validate(ticket, service)
					assertion.getPrincipal match {
						case a: AttributePrincipalImpl => {
							val uid = a.getName
							setUser(uid)
							createAndShowMainWindow
							// redirect to application's base URL
							val response = new DownloadStream(null, null, null)
							response.setParameter("Location", getURL.toString)
							response
						}
						case _ => throw new RuntimeException
					}
				}
				else
				{
					null
				}
			}
		})
		setMainWindow(loginWindow)
	}

	private def createAndShowMainWindow = {
		createAndInitViewProvider
		mainView = new MainViewImpl(this, viewProvider)
		mainView.addListener(this)
		// Set new main window
		val mainWindow = new Window(mainView.getDisplayName(), mainView.getViewComponent())
		setMainWindow(mainWindow)
	}

	private def createAndInitViewProvider = {
		viewProvider = new DefaultViewProvider()
		viewProvider.addPreinitializedView(new UserFormViewImpl(this), UserFormView.VIEW_ID)
	}

	override def handleViewEvent(event: ViewEvent) {
		event match {
			case event: UserLoggedOutEvent => close
		}
	}
	
	def getDirectoryService = applicationContext.getBean("directoryService", classOf[LdapDirectoryService])

	def getConsentDao = consentDao

}
