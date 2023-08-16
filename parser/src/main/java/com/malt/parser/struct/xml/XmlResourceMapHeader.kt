package com.malt.parser.struct.xml

import com.malt.parser.struct.ChunkHeader

/**
 * @author malt
 */
class XmlResourceMapHeader(chunkType: Int, headerSize: Int, chunkSize: Long) :
    ChunkHeader(chunkType, headerSize, chunkSize)