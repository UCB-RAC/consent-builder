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

package edu.berkeley.rac.ophs.consentBuilder.output

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import org.docx4j.jaxb.Context
import org.docx4j.openpackaging.contenttype.ContentType
import org.docx4j.openpackaging.io.LoadFromZipNG
import org.docx4j.openpackaging.io.SaveToZipFile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart
import org.docx4j.openpackaging.parts.PartName
import org.docx4j.wml.BooleanDefaultTrue
import org.docx4j.wml.ObjectFactory
import com.vaadin.terminal.StreamResource.StreamSource
import edu.berkeley.rac.ophs.consentBuilder.model.ConsentAnswer.unwrap
import edu.berkeley.rac.ophs.consentBuilder.model.Consent
import javax.persistence.Embeddable
import javax.persistence.Entity
import scala.util.matching.Regex
import edu.berkeley.rac.ophs.consentBuilder.ConsentBuilderApplication
import edu.berkeley.rac.ophs.consentBuilder.ui.forms.FormList


//class DocumentSource(consent: Consent) extends StreamSource {
class DocumentSource(formList: FormList, consent: Consent) extends StreamSource {
  
//  val answersVersion = (application getFormContext) getBean("answersVersion")
//  if (answersVersion != null)
//  {
//    Option(consent answersVersion) match
//    {
//      case None =>
//        throw new IllegalArgumentException("Tried to apply template version "
//            + answersVersion + " to unversioned answer data")
//      case Some(version) =>
//        if (version != answersVersion)
//          throw new IllegalArgumentException("Tried to apply template version "
//              + answersVersion + " to answers versioned " + (consent answersVersion))
//    }
//  }
//  
  
  if (!(formList isCurrent consent))
    throw new IllegalArgumentException("Some consent answers are out of date")
    
  /*
   * TODO: convert this direct access to the consent textanswers map into calls to the local getAnswer,
   *     which calls the consent#getAnswerText wrapper method.
   */
  val answers = consent getTextAnswers
  
  val wordMLPackage = 
    (new LoadFromZipNG())
    .get(
      getClass
      getResourceAsStream "/edu/berkeley/rac/ophs/consentBuilder/blank consent.docx"
      )
    .asInstanceOf[WordprocessingMLPackage]
  val mainPart = wordMLPackage getMainDocumentPart
  val objectFactory = new ObjectFactory()
  
  val htmlContentType = new ContentType("text/html")
  val docxContentType = new ContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml")
  
  (wordMLPackage getContentTypeManager) addDefaultContentType("html", "text/html")
  
    
// START OF DOCUMENT CONTENT
  
  //addParagraph("")
  addStyledParagraph("Title", "CONSENT TO PARTICIPATE IN RESEARCH")
  addStyledParagraph(
    "Title"
    , (answers get "studyTitle") +
      (getAnswer("studySubtitle") match
      {
        case Some(answer) => " (" + answer + ")"
        case None => ""
      })
    )
    
  addStyledParagraph("Heading1", "Introduction")
  
  addParagraph (
    (answers get "si") match
    {
      case null =>
        "My name is " + (answers get "pi") + ". I am a faculty member at the University of California, Berkeley, in " +
        (answers get "department") + ".  I am planning to conduct a research study, which I invite you to take part in."
      case researcher =>
        "My name is " + researcher + ". I am" + (
          (answers get "affiliation") match
          {
            case null => " "
            case affiliation => " " + affiliation + " "
          }
          ) + 
        "working with " + (answers get "pi") + " in " + (answers get "department") +
        " at the University of California, Berkeley.  We are planning to conduct a research study, which I invite you to take part in."
    })
  addHTMLAnswer("inclusion")
  
  addStyledParagraph("Heading1", "Purpose")
  
  addHTMLAnswer("purpose")
  addHTMLAnswer("numberSubjects")
  
  addStyledParagraph("Heading1", "Procedures")
  
  addParagraph("If you agree to be in this study, you will be asked to do the following:")
  Option(answers get "procScreen") match
  {
    case Some(screeningProcedures) =>
    {
      addStyledParagraph("Heading2", "Before you begin the main part of the study:")
      
      addParagraph("You will need to have the following screening tests/procedures to find out if you can be in the main part of the study.")
      
      addHTMLSnippet(
        (new HTMLSection())
          addHTMLAnswer("screenBlood")
          addStringItem(
            if (screeningProcedures contains "Urine")
              "Urine sample: You will be asked to give a urine sample for lab tests."
            else null
            )
          addHTMLAnswer("screenPregnancy")
          addStringItem(
            if (screeningProcedures contains "Physical")
              "Physical examination: You will have a physical examination, similar to those done for regular medical care."
            else null
            )
          addStringItem(
            if (screeningProcedures contains "Chart")
              "Medical chart: Your medical chart will be reviewed by the researchers."
            else null
            )
          addHTMLAnswer("screenHIV")
          addHTMLAnswer("screenOther")
          asHTML,
        "Screening",
        false)
      
      addParagraph("If the screening shows that you can be in the study and you choose to continue, this is what will happen next:")
      addStyledParagraph("Heading2", "During the main part of the study:")
    }
    case None =>  {}
  }
  println (addHTMLSnippet(
    (new HTMLSection())
      addHTMLAnswer("procRandomztn")
      addHTMLAnswer("procSurvey")
      addHTMLAnswer("procInterview")
      addHTMLAnswer("procFocusGroup")
      addHTMLAnswer("procObservation")
      addHTMLAnswer("procBehavioralTasks")
      addHTMLAnswer("procAudio")
      addHTMLAnswer("procVideo")
      addHTMLAnswer("procSBOther")
      addHTMLAnswer("procBlood")
      addHTMLAnswer("procXRay")
      addHTMLAnswer("procMRI")
      addHTMLAnswer("procCTScan")
      addHTMLAnswer("procDNA")
      addHTMLAnswer("procMedicalOther")
      asHTML,
    "Procdedures",
    false) )
    
  addStyledParagraph("Heading2", "Study time")
  
  addHTMLAnswer("ttlTime")
  
  addStyledParagraph("Heading2", "Study location")
  
  addHTMLAnswer("location")
  
  addStyledParagraph("Heading1", "Benefits")
  
  val optionalQs = answers get "optionalQuestions"
  val hasDirectBenefits = optionalQs != null && (optionalQs contains "DirectBenefits")
  addHTMLSnippet(
    (new HTMLSection())
      if (hasDirectBenefits)
        addHtmlAnswer("benefitP")
      else
        addParagraph("There is no direct benefit to you expected from taking part in this study.")
      addHTMLAnswer("benefitS")
      asHTML,
    "Benefits",
    false)
  
  addStyledParagraph("Heading1", "Risks/Discomforts")
  
  val medRisks = answers get "risksMedical"
  val hasMedRisks = medRisks != null
  addHTMLSnippet(
    (new HTMLSection())
      addHTMLAnswer("risksSB")
      addStringItem(
        if (hasMedRisks && (medRisks contains "Blood"))
          "Blood draw (venipuncture): Drawing blood may cause temporary discomfort from the needle stick, bruising, or very rarely, infection."
        else null)
      addStringItem(
        if (hasMedRisks && (medRisks contains "X-ray"))
          "Radiation (x-ray): The amount of radiation you will be exposed to is relatively small.  Such doses of radiation may be potentially harmful, but the risks are so small that they are difficult to measure.  If you have already had many x-rays or are especially concerned with radiation exposure, you should discuss this with the researchers before agreeing to be in the study."
        else null)
      addHTMLAnswer("risksMRI")
      addHTMLAnswer("risksCT")
      addStringItem(
        if (hasMedRisks && (medRisks contains "HIV"))
          "HIV testing risks: Being tested for HIV may cause anxiety regardless of the test results.  A positive test indicates that you have been infected with the HIV virus, but no one knows for certain when, if ever, you will become sick with AIDS or a related condition.  Receiving positive results may make you very upset.  If other people learned about your positive test results, there is a risk that you could be treated unfairly or badly, and even have trouble obtaining insurance or employment.  To the extent permitted by law, we will keep your test results confidential and will not release them to anyone without your written permission.  (Note:  In California, the testing lab is required to report identified positive results to public health authorities.)"
        else null)
      addStringItem(
        if (hasMedRisks && (medRisks contains "Randomization"))
          "Randomization risks: Because you will be assigned to a treatment program by chance, the treatment you receive may prove to be less effective or to have more side effects than the other study treatment(s) or other available treatments."
        else null)
      addHTMLAnswer("risksPlacebo")
      addHTMLAnswer("risksUnknown")
      addStringItem("Breach of confidentiality: As with all research, there is a chance" +
  	    " that confidentiality could be compromised; however, we are taking precautions to minimize this risk.")
  	asHTML,
  	"Risks",
  	false)
  
  addStyledParagraph("Heading1", "Confidentiality")
  
//  val dataRelease: String = answers get "dataRelease"
//  dataRelease match
//  {
//    case "Yes" =>
//      addParagraph ("Your study data will be handled as confidentially as possible. If results of this study are published or presented, individual names and other personally identifiable information will not be used unless you give explicit permission for this below.")
//    case _ =>
//      addParagraph ("Your study data will be handled as confidentially as possible. If results of this study are published or presented, individual names and other personally identifiable information will not be used.")
//  }
  
  if ((answers get "dataRelease") != null && ((answers get "dataRelease") contains "Y"))
    addParagraph ("Your study data will be handled as confidentially as possible. If results of this study are published or presented, individual names and other personally identifiable information will not be used unless you give explicit permission for this below.")
  else
    addParagraph ("Your study data will be handled as confidentially as possible. If results of this study are published or presented, individual names and other personally identifiable information will not be used.")
  
  addParagraph("To minimize the risks to confidentiality, we will do the following:")
  
  addHTMLSnippet(
    (new HTMLSection())
      addHTMLAnswer("dataID")
      addHTMLAnswer("dataStorage")
      addHTMLAnswer("dataAccess")
      asHTML,
    "Confidentiality",
    false)
  if ((answers get "coc") != null && ((answers get "coc") contains "Y"))
  {
    addStyledParagraph("Heading2", "Certificate of Confidentiality:")
    addParagraph("To help us protect your privacy, we have obtained a Certificate of Confidentiality from the National Institutes of Health (NIH). With this Certificate, researchers cannot be forced to disclose information that may identify you, even by a court subpoena, in any federal, state, or local civil, criminal, administrative, legislative, or other proceedings.")
    addParagraph("Exceptions: A Certificate of Confidentiality does not prevent researchers from disclosing certain information about you for legal or ethical reasons. For example, we will report information about child abuse, elder abuse, or intent to hurt yourself or others.  If an insurer, employer, or other person obtains your written consent to receive research information, we cannot use the Certificate to withhold that information. In addition, the Certificate may not be used to withhold information from the federal government needed for auditing or evaluating federally funded projects or information needed by the FDA, e.g., for quality assurance or data analysis.")
  }
  addStyledParagraph("Heading2", "Future use of study data:")
  addHTMLAnswer("dataRetention")
  if ((answers get "specimen") != null && ((answers get "specimen") contains "Y"))
  {
    addStyledParagraph("Heading2", "Future use of study specimens:")
    addParagraph("If you consent to give blood or tissue specimens (including cheek swab and saliva samples) as part of this study, these specimens will become the property of the University of California. The specimens and the DNA they contain may be used in this research and in other research, and may be shared with other organizations. The specimens could lead to discoveries or inventions that may be of value to the University of California or to other organizations. Under state law, you do not have any right to money or other compensation stemming from products that may be developed from the specimens.")
  }
  
  if ((answers get "alternatives") != null && ((answers get "alternatives").trim.length > 0))
  {
    addStyledParagraph("Heading1", "Alternatives to Participation")
    addHTMLAnswer("alternatives")
  }
  
  addStyledParagraph("Heading1", "Compensation/Payment")
  if ((answers get "compensation") == null)
    addParagraph("You will not be compensated for your participation in this study.")
  else
    addHTMLAnswer("compensation")
  
  addStyledParagraph("Heading1", "Costs")
  if ((answers get "costs") == null)
    addParagraph("You will not be charged for any of the study activities.")
  else
    addHTMLAnswer("costs")

  if ((answers get "tcForInjury") != null)
  {
    addStyledParagraph("Heading1", "Treatment and Compensation for Injury")
    addHTMLAnswer("tcForInjury")
  }
  
  addStyledParagraph("Heading1", "Rights")
  
  addStyledParagraph("Heading2", "Participation in research is completely voluntary.")
  addParagraph("You have the right to decline to participate or to withdraw at any point in this study without penalty or loss of benefits to which you are otherwise entitled.")
  
  addStyledParagraph("Heading1", "Questions")
  
  addHTMLAnswer("contact")
  addParagraph("If you have any questions or concerns about your rights and treatment as a research subject, you may contact the office of UC Berkeley's Committee for the Protection of Human Subjects, at 510-642-7461 or subjects@berkeley.edu.")
  
  addStyledParagraph("Heading1", "Consent")
  
  val researchType = answers get "researchType"
  if (researchType != null && (researchType contains "Biomedical")) {
    addParagraph("You have been given a copy of this consent form and of the Medical Research Subject's Bill of Rights to keep.")
  }
  else
  {
    addParagraph("You have been given a copy of this consent form to keep.")
  }
  val hipaa = answers get "hipaa"
  if (hipaa != null && (hipaa contains "Yes")) addParagraph ("You will be asked to sign a separate form authorizing access, use, creation, or disclosure of health information about you.")
  addAltChunk(
    ( () =>
      {
        val mainSig = new AlternativeFormatInputPart (new PartName("/mainsigs.docx") )
        mainSig setBinaryData (getClass getResourceAsStream "/edu/berkeley/rac/ophs/consentBuilder/mainsigs.docx")
        mainSig setContentType docxContentType
        mainSig
      }
    ) (),
    false)
  
    
// END OF DOCUMENT CONTENT
    
    
  private def addParagraph(paragraph: String) { mainPart addParagraphOfText paragraph }
  
  private def addStyledParagraph(style: String, paragraph: String) { mainPart addStyledParagraphOfText(style, paragraph) }
  
  private def addHTMLAnswer(key: String)
  {
    getAnswer(key) match
    {
      case None => {}
      case Some(answer) => addHTMLSnippet(answer, key, false) 
    }
  }
  
  private def addHTMLSnippet(snippet: String, label: String, preserveFormat: Boolean): String =
  {
    if (snippet != null && snippet.trim.length > 0)
    {
      val html = "<html><head><title>" + label + "</title></head><body style=\"Normal\">" + (snippet trim) + "</body></html>"
	  val afiPart = new AlternativeFormatInputPart(new PartName("/" + label + ".html") )
      afiPart setBinaryData (html getBytes)
	  afiPart setContentType htmlContentType
    
      addAltChunk(afiPart, preserveFormat)
      html
    }
    else
    {
      snippet
    }
  }
  
  private def addAltChunk(afiPart: AlternativeFormatInputPart, preserveFormat: Boolean)
  {
    val altChunkRel = mainPart addTargetPart afiPart
	val ac = (Context getWmlObjectFactory) createCTAltChunk
	
	val acpr = (Context getWmlObjectFactory) createCTAltChunkPr
	val valFalse = new BooleanDefaultTrue()
    valFalse setVal preserveFormat
    acpr setMatchSrc valFalse
    ac setAltChunkPr acpr
	
	ac setId (altChunkRel getId)
	mainPart addObject ac 
  }
  
  private def getAnswer(key: String): Option[String] = consent getAnswerText key
  
//  private def insertOPHSNumber: WordprocessingMLPackage
//  {
//    
//  }
  
  def getStream: InputStream =
  {
    val saver = new SaveToZipFile(wordMLPackage)
    val outputstream = new ByteArrayOutputStream()
    saver save outputstream
    return new ByteArrayInputStream(outputstream toByteArray) 
  }
  
  private class HTMLSection
  {
    private var items: List[String] = List[String]()
    private val PTag: Regex = """<p>\s?</p>""".r
    
    def addHTMLAnswer(key: String): HTMLSection =
    {
      if (key != null
        && (key length) > 0
        && (answers get key) != null
        && answers.get(key).toString.trim.length > 0
        )
      {
        items = items :+ ((answers get key toString) trim)
      }
      this
    }
    
    def addStringItem(item: String): HTMLSection =
    {
      if (item != null && (item length) > 0) items = items :+ ("<p>" + item + "</p>")
      this
    }
    
    def asHTML: String =
    {
      (items length) match
      {
        case 0 => ""
        case 1 => items head
        case _ =>
          items.foldLeft[String]("<ul style=\"ListBullet\">")(
            (ashtml: String, item: String) => (
              ashtml
              + "<li>\n"
              + item
              //+ PTag.replaceAllIn(item, "<br />")
              + "</li>\n"
              )
            ) + "</ul>\n"
      }
      
    }
  
  }
  
}
