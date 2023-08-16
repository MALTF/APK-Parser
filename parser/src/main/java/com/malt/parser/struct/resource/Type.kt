package com.malt.parser.struct.resource

import com.malt.parser.struct.StringPool
import com.malt.parser.utils.Buffers.position
import com.malt.parser.utils.Buffers.readUInt
import com.malt.parser.utils.Buffers.readUShort
import com.malt.parser.utils.ParseUtils.readResValue
import java.nio.ByteBuffer
import java.util.Locale


/**
 * @author malt
 */
class Type(@JvmField val header: TypeHeader) {
    var name: String? = null
    var id: Short = header.id.toShort()
    var locale: Locale
    var keyStringPool: StringPool? = null
    lateinit var buffer: ByteBuffer
    lateinit var offsets: LongArray
    var stringPool: StringPool? = null

    // see Densities.java for values
    val density: Int

    init {
        val config = header.config
        locale = Locale(config.language, config.country)
        density = config.density.toInt()
    }

    fun getResourceEntry(id: Int): ResourceEntry? {
        if (id >= offsets.size) {
            return null
        }
        if (offsets[id] == TypeHeader.NO_ENTRY) {
            return null
        }

        // read Resource Entries
        buffer.let { position(it, offsets[id]) }
        return readResourceEntry()
    }

    private fun readResourceEntry(): ResourceEntry {
        val beginPos = buffer.position().toLong()
        // size is always 8(simple), or 16(complex)
        // size is always 8(simple), or 16(complex)
        val resourceEntrySize = readUShort(buffer)
        val resourceEntryFlags = readUShort(buffer)
        val keyRef = buffer.int.toLong()
        val resourceEntryKey = keyStringPool?.get(keyRef.toInt())!!
        return if (resourceEntryFlags and ResourceEntry.FLAG_COMPLEX != 0) {
            // Resource identifier of the parent mapping, or 0 if there is none.
            val parent = readUInt(buffer)
            val count = readUInt(buffer)
            //            resourceMapEntry.setParent(parent);
            //            resourceMapEntry.setCount(count);
            position(buffer, beginPos + resourceEntrySize)
            //An individual complex Resource entry comprises an entry immediately followed by one or more fields.
            val resourceTableMaps = arrayOfNulls<ResourceTableMap>(count.toInt())
            for (i in 0 until count) {
                resourceTableMaps[i.toInt()] = readResourceTableMap()
            }
            ResourceMapEntry(
                resourceEntrySize,
                resourceEntryFlags,
                resourceEntryKey,
                parent,
                count,
                resourceTableMaps
            )
        } else {
            position(buffer, beginPos + resourceEntrySize)
            val resourceEntryValue = readResValue(buffer, stringPool)
            ResourceEntry(
                resourceEntrySize, resourceEntryFlags, resourceEntryKey, resourceEntryValue
            )
        }
    }

    private fun readResourceTableMap(): ResourceTableMap {
        val resourceTableMap = ResourceTableMap()
        resourceTableMap.nameRef = readUInt(buffer)
        resourceTableMap.resValue = buffer.let { readResValue(it, stringPool) }
        if (resourceTableMap.nameRef and 0x02000000L != 0L) {
            //read arrays
        } else if (resourceTableMap.nameRef and 0x01000000L != 0L) {
            // read attrs
        } else {
        }
        return resourceTableMap
    }

    override fun toString(): String {
        return "Type{name='$name', id=$id, locale=$locale}"
    }
}