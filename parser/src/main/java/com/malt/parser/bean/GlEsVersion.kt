package com.malt.parser.bean

/**
 * the glEsVersion apk used.
 */
class GlEsVersion(val major: Int, val minor: Int, val isRequired: Boolean) {

    override fun toString(): String {
        return major.toString() + "." + minor
    }
}