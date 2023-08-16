package com.malt.parser.bean

/**
 * the permission used by apk
 */
class UseFeature(val name: String, val isRequired: Boolean) {

    override fun toString(): String {
        return name
    }
}