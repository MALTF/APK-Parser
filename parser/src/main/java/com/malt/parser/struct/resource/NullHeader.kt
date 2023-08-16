package com.malt.parser.struct.resource

import com.malt.parser.struct.ChunkHeader
import com.malt.parser.struct.ChunkType

class NullHeader(headerSize: Int, chunkSize: Long) :
    ChunkHeader(ChunkType.NULL, headerSize, chunkSize)