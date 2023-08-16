package com.malt.parser.struct.resource

import com.malt.parser.struct.ChunkHeader
import com.malt.parser.struct.ChunkType
import com.malt.parser.utils.Buffers
import com.malt.parser.utils.Unsigned
import java.nio.ByteBuffer

/**
 * resource file header
 *
 * @author malt
 */
class ResourceTableHeader(headerSize: Int, chunkSize: Long, @JvmField val buffer: ByteBuffer) :
    ChunkHeader(ChunkType.TABLE, headerSize, chunkSize) {

    // The number of ResTable_package structures. uint32
    var packageCount = 0
        get() = Unsigned.toLong(field).toInt()

    init {
        packageCount = Unsigned.toUInt(Buffers.readUInt(buffer))
    }
}