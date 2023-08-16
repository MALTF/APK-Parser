package com.malt.parser.struct.xml

import com.malt.parser.struct.ChunkHeader
import com.malt.parser.utils.Buffers.readUInt
import java.nio.ByteBuffer


/**
 * @author malt
 */
class XmlNodeHeader(chunkType: Int, headerSize: Int, chunkSize: Long, buffer: ByteBuffer) :
    ChunkHeader(chunkType, headerSize, chunkSize) {
    /**
     * Line number in original source file at which this element appeared.
     */
    var lineNum = 0

    /**
     * Optional XML comment string pool ref, -1 if none
     */
    var commentRef = 0

    init {
        lineNum = readUInt(buffer).toInt()
        commentRef = readUInt(buffer).toInt()
    }
}