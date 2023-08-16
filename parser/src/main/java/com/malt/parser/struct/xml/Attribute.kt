package com.malt.parser.struct.xml

import com.malt.parser.struct.ResourceValue
import com.malt.parser.struct.resource.ResourceTable
import com.malt.parser.utils.ResourceLoader
import java.util.Locale

/**
 * xml node attribute
 *
 * @author malt
 */
class Attribute (
    @JvmField val namespace: String, @JvmField val name: String,
    /**
     * The original raw string value of Attribute
     */
    @JvmField val rawValue: String?,
    /**
     * Processed typed value of Attribute
     */
    @JvmField val typedValue: ResourceValue?
){
    /**
     * the final value as string
     */
    @JvmField
    var value: String? = null
    fun toStringValue(resourceTable: ResourceTable, locale: Locale): String? {
        return if (rawValue != null) {
            rawValue
        } else {
            val typedValue = typedValue
            if (typedValue != null) {
                typedValue.toStringValue(resourceTable, locale)
            } else {
                // something happen;
                ""
            }
        }
    }

    /**
     * These are attribute resource constants for the platform; as found in android.R.attr
     *
     * @author malt
     */
    object AttrIds {
        private val ids = ResourceLoader.loadSystemAttrIds()

        @JvmStatic
        fun getString(id: Long): String {
            return ids[id.toInt()] ?: "AttrId:0x${java.lang.Long.toHexString(id)}"
        }
    }

    override fun toString(): String {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                '}'
    }
}