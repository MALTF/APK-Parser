package com.malt.parser.struct.xml

/**
 * @author malt
 */
class XmlNamespaceStartTag {
    var prefix: String? = null
    var uri: String? = null
    override fun toString(): String {
        return "$prefix=$uri"
    }
}