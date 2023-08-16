package com.malt.parser.struct.xml

import com.malt.parser.struct.ChunkHeader

/**
 * Null header.
 *
 * @author malt
 */
class NullHeader(chunkType: Int, headerSize: Int, chunkSize: Long) :
    ChunkHeader(chunkType, headerSize, chunkSize)