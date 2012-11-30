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

import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import com.vaadin.terminal.StreamResource
import edu.berkeley.rac.ophs.consentBuilder.output.DocumentSource
import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication
import com.vaadin.terminal.DownloadStream
import edu.berkeley.rac.vaadin.ui.AlertModalWindow
import edu.berkeley.rac.ophs.consentBuilder.ui.forms.FormList


trait DocumentDownloader {
  
  	def exportDocx(application: ConsentBuilderApplication, consent: Consent)
	{
	  try
	  {
	    val documentSource: StreamResource.StreamSource =
	      new DocumentSource(
	        application.getFormContext.getBean("formList").asInstanceOf[FormList], 
	        consent
	        )
	    val streamResource: StreamResource = new StreamResource(documentSource, ((consent getTextAnswers) get("studyTitle")) + ".docx", application)
	  
	    streamResource setMIMEType "application/octet-stream"
	    val dlstream: DownloadStream = streamResource getStream()
	    dlstream setParameter ("Content-Disposition", "attachment; filename=\"" + (streamResource getFilename) + "\"")
	    dlstream setParameter ("Content-Length", Integer.toString((dlstream getStream) available))
	  
	    streamResource.setCacheTime(5 * 1000); // no cache (<=0) does not work with IE8, (in milliseconds)
        (application getMainWindow) open (streamResource, "_top");
      }
	  catch
	  {
	    case e:IllegalArgumentException => application.getWindow("main").addWindow(new AlertModalWindow("title", "text"))
	  }

	}

}