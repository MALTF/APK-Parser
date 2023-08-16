package com.malt.parser.struct.xml

/**
 * @author malt
 */
class XmlNamespaceEndTag(
    @JvmField val prefix: String,
    @JvmField val uri: String
) {
    override fun toString(): String {
        return "$prefix=$uri"
    }
}