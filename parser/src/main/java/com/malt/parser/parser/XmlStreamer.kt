package com.malt.parser.parser

import com.malt.parser.struct.xml.XmlCData
import com.malt.parser.struct.xml.XmlNamespaceEndTag
import com.malt.parser.struct.xml.XmlNamespaceStartTag
import com.malt.parser.struct.xml.XmlNodeEndTag
import com.malt.parser.struct.xml.XmlNodeStartTag

/**
 * callback interface for parse binary xml file.
 *
 * @author malt
 */
interface XmlStreamer {
    fun onStartTag(xmlNodeStartTag: XmlNodeStartTag)
    fun onEndTag(xmlNodeEndTag: XmlNodeEndTag)
    fun onCData(xmlCData: XmlCData)
    fun onNamespaceStart(tag: XmlNamespaceStartTag)
    fun onNamespaceEnd(tag: XmlNamespaceEndTag)
}