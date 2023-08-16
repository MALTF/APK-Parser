package com.malt.parser.parser

import com.malt.parser.struct.xml.XmlCData
import com.malt.parser.struct.xml.XmlNamespaceEndTag
import com.malt.parser.struct.xml.XmlNamespaceStartTag
import com.malt.parser.struct.xml.XmlNodeEndTag
import com.malt.parser.struct.xml.XmlNodeStartTag

/**
 * Parse adaptive icon xml file.
 *
 * @author malt
 */
class AdaptiveIconParser : XmlStreamer {
    var foreground: String? = null
        private set
    var background: String? = null
        private set

    override fun onStartTag(xmlNodeStartTag: XmlNodeStartTag) {
        if ("background" == xmlNodeStartTag.name) {
            background = getDrawable(xmlNodeStartTag)
        } else if ("foreground" == xmlNodeStartTag.name) {
            foreground = getDrawable(xmlNodeStartTag)
        }
    }

    private fun getDrawable(xmlNodeStartTag: XmlNodeStartTag): String? {
        val attributes = xmlNodeStartTag.attributes
        if (attributes != null) {
            for (attribute in attributes.attributes) {
                if (attribute != null) {
                    if ("drawable" == attribute.name) {
                        return attribute.value
                    }
                }
            }
        }
        return null
    }

    override fun onEndTag(xmlNodeEndTag: XmlNodeEndTag) {}
    override fun onCData(xmlCData: XmlCData) {}
    override fun onNamespaceStart(tag: XmlNamespaceStartTag) {}
    override fun onNamespaceEnd(tag: XmlNamespaceEndTag) {}
}