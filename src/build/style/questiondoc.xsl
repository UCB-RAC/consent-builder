<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
	    <html>
	        <head>
	            <style type="text/css">
	                @media screen {
	                    body {
	                        margin-left: 10em;
	                        font-family: sans-serif;
	                    }
	                }
	                h1 {
	                    margin-top: 1.5em;
	                }
	                .question-details {
	                    margin-left: 3em;
	                }
	                .question-details p {
	                    width: 35em;
	                }
	            </style>
	        </head>
	        <body>
                <!--
                    THIS WORKS: <xsl:apply-templates select=".//*[local-name() = 'bean']" />
                    ...but is not in order.
                 -->
                <xsl:for-each select=".//*[local-name() = 'ref']">
                    <xsl:variable name="formName" select="@local" />                    
                    <xsl:apply-templates select="/*[local-name() = 'beans']//*[local-name() = 'bean' and @id = $formName]" />
                </xsl:for-each>
	        </body>
	    </html>
	</xsl:template>
	
	<xsl:template match="*[local-name() = 'bean' and @class='edu.berkeley.rac.ophs.consentBuilder.ui.forms.MultiSelectForm']">
	    <div class="question">
	    <h1><xsl:value-of select="*[local-name() = 'constructor-arg'][2]/@value" /></h1>
	    <div class="question-details">
        <p><xsl:value-of disable-output-escaping="yes" select="*[local-name() = 'constructor-arg'][3]/@value | *[local-name() = 'constructor-arg'][3]/*[local-name() = 'value']" /></p>
        <h3>Options</h3>
        <ul>
        <xsl:for-each select="*[local-name() = 'constructor-arg'][1]//*[local-name() = 'entry']">
        <li><xsl:value-of select="@value" /></li>        
        </xsl:for-each>
        </ul>
        </div>
        </div>
	</xsl:template>

    <xsl:template match="*[local-name() = 'bean' and @class='edu.berkeley.rac.ophs.consentBuilder.ui.forms.SingleQuestionForm']">
        <div class="question">
        <h1><xsl:value-of select="*[local-name() = 'constructor-arg'][2]/@value" /></h1>
        <div class="question-details">
        <p><xsl:value-of disable-output-escaping="yes" select="*[local-name() = 'constructor-arg'][3]/@value | *[local-name() = 'constructor-arg'][3]/*[local-name() = 'value']" /></p>
        <xsl:if test="*[local-name() = 'property' and @name='defaultText']/*[local-name() = 'value']">
            <h3>Default response text</h3>
            <p><xsl:value-of disable-output-escaping="yes" select="*[local-name() = 'property' and @name='defaultText']/*[local-name() = 'value']" /></p>
        </xsl:if>
        </div>
        </div>
    </xsl:template>

    <xsl:template match="*" />

</xsl:stylesheet>