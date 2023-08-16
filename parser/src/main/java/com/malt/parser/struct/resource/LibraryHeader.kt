package com.malt.parser.struct.resource

import com.malt.parser.struct.ChunkHeader
import com.malt.parser.struct.ChunkType
import com.malt.parser.utils.Buffers
import com.malt.parser.utils.Unsigned.ensureUInt
import java.nio.ByteBuffer

/**
 * Table library chunk header
 *
 * @author malt
 */
class LibraryHeader(headerSize: Int, chunkSize: Long, buffer: ByteBuffer) :
    ChunkHeader(ChunkType.TABLE_LIBRARY, headerSize, chunkSize) {
    /**
     * A package-id to package name mapping for any shared libraries used
     * in this resource table. The package-id's encoded in this resource
     * table may be different than the id's assigned at runtime. We must
     * be able to translate the package-id's based on the package name.
     */
    /**
     * uint32 value, The number of shared libraries linked in this resource table.
     */
    val count: Int

    init {
        count = ensureUInt(Buffers.readUInt(buffer))
    }
}