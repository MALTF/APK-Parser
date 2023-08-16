package com.malt.parser.bean

import java.io.Serializable

/**
 * Android adaptive icon, from android 8.0
 */
class AdaptiveIcon(
    /**
     * The foreground icon
     */
    val foreground: Icon?,
    /**
     * The background icon
     */
    val background: Icon?
) : IconFace, Serializable {

    override fun toString(): String {
        return "AdaptiveIcon{" +
                "foreground=" + foreground +
                ", background=" + background +
                '}'
    }

    override val isFile: Boolean
        get() = foreground!!.isFile
    override val data: ByteArray?
        get() = foreground?.data
    override val path: String?
        get() = foreground?.path

    companion object {
        private const val serialVersionUID = 4185750290211529320L
    }
}