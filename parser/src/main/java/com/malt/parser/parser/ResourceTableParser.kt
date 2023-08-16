package com.malt.parser.parser

import android.util.Log
import com.malt.parser.exception.ParserException
import com.malt.parser.struct.ChunkHeader
import com.malt.parser.struct.ChunkType
import com.malt.parser.struct.StringPool
import com.malt.parser.struct.StringPoolHeader
import com.malt.parser.struct.resource.LibraryEntry
import com.malt.parser.struct.resource.LibraryHeader
import com.malt.parser.struct.resource.NullHeader
import com.malt.parser.struct.resource.PackageHeader
import com.malt.parser.struct.resource.ResourcePackage
import com.malt.parser.struct.resource.ResourceTable
import com.malt.parser.struct.resource.ResourceTableHeader
import com.malt.parser.struct.resource.Type
import com.malt.parser.struct.resource.TypeHeader
import com.malt.parser.struct.resource.TypeSpec
import com.malt.parser.struct.resource.TypeSpecHeader
import com.malt.parser.utils.Buffers
import com.malt.parser.utils.Pair
import com.malt.parser.utils.ParseUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale

/**
 * Parse android resource table file.
 *
 * @author malt
 * @see [ResourceTypes.h](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/libs/androidfw/include/androidfw/ResourceTypes.h)
 *
 * @see [ResourceTypes.cpp](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/libs/androidfw/ResourceTypes.cpp)
 */
class ResourceTableParser(buffer: ByteBuffer) {
    /**
     * By default the data buffer Chunks is buffer little-endian byte order both at runtime and when stored buffer files.
     */
    private val byteOrder = ByteOrder.LITTLE_ENDIAN
    private var stringPool: StringPool? = null
    private val buffer: ByteBuffer

    // the resource table file size
    var resourceTable: ResourceTable? = null

    val locales: MutableSet<Locale?>

    init {
        this.buffer = buffer.duplicate()
        this.buffer.order(byteOrder)
        locales = HashSet()
    }

    /**
     * parse resource table file.
     */
    fun parse() {
        // read resource file header.
        val resourceTableHeader = readChunkHeader() as ResourceTableHeader
        // read string pool chunk
        stringPool = ParseUtils.readStringPool(buffer, readChunkHeader() as StringPoolHeader)
        resourceTable = ResourceTable(stringPool)
        val packageCount = resourceTableHeader.packageCount;
        if (packageCount != 0) {
            var packageHeader = readChunkHeader() as PackageHeader
            Log.d(
                "packageHeader",
                "packageHeader: ${packageHeader.name} packageCount:$packageCount id:${packageHeader.id}"
            )
            for (i in 0 until packageCount) {
                val pair = readPackage(packageHeader)
                val right = pair.right
                Log.d("readPackage", "parse.left: ${pair.left} parse.right: $right")
                resourceTable?.addPackage(pair.left)
                if (right != null) {
                    packageHeader = right
                }
            }
        }
    }

    // read one package
    private fun readPackage(packageHeader: PackageHeader): Pair<ResourcePackage, PackageHeader> {
        val pair = Pair<ResourcePackage, PackageHeader>()
        // read packageHeader
        val resourcePackage = ResourcePackage(packageHeader)
        resourcePackage.let { pair.setLeft(it) }
        val beginPos = buffer.position().toLong()
        // read type string pool
        if (packageHeader.typeStrings > 0) {
            Buffers.position(
                buffer, beginPos + packageHeader.typeStrings - packageHeader.headerSize
            )
            resourcePackage.typeStringPool = ParseUtils.readStringPool(
                buffer, readChunkHeader() as StringPoolHeader
            )
        }

        //read key string pool
        if (packageHeader.keyStrings > 0) {
            Buffers.position(
                buffer, beginPos + packageHeader.keyStrings - packageHeader.headerSize
            )
            resourcePackage.keyStringPool = ParseUtils.readStringPool(
                buffer, readChunkHeader() as StringPoolHeader
            )
        }
        Log.d(
            "readPackage",
            "hasRemaining: ${buffer.hasRemaining()} beginPos:$beginPos typeStrings: ${packageHeader.typeStrings}" +
                    " keyStrings:${packageHeader.keyStrings} keyStringPool:${resourcePackage.keyStringPool.toString()}"
        )
        outer@ while (this.buffer.hasRemaining()) {
            val chunkHeader = readChunkHeader()
            val chunkBegin = buffer.position().toLong()
            Log.d("chunkHeader", "chunkHeader: ${chunkHeader.chunkType} chunkBegin:$chunkBegin")
            when (chunkHeader.chunkType) {
                // 514
                ChunkType.TABLE_TYPE_SPEC.toShort() -> {
                    val typeSpecHeader = chunkHeader as TypeSpecHeader
                    val entryFlags = LongArray(typeSpecHeader.entryCount)
                    var i = 0
                    while (i < typeSpecHeader.entryCount) {
                        entryFlags[i] = Buffers.readUInt(buffer)
                        i++
                    }
                    //id start from 1
                    val typeSpecName = resourcePackage.typeStringPool?.get(typeSpecHeader.id - 1)
                    val typeSpec = typeSpecName?.let { TypeSpec(typeSpecHeader, entryFlags, it) }
                    typeSpec?.let { resourcePackage.addTypeSpec(it) }
                    Buffers.position(buffer, chunkBegin + typeSpecHeader.bodySize)
                }

                // 513
                ChunkType.TABLE_TYPE.toShort() -> {
                    val typeHeader = chunkHeader as TypeHeader
                    // read offsets table
                    val offsets = LongArray(typeHeader.entryCount)
                    var i = 0
                    while (i < typeHeader.entryCount) {
                        offsets[i] = Buffers.readUInt(buffer)
                        i++
                    }
                    val type = Type(typeHeader)
                    type.name = resourcePackage.typeStringPool?.get(typeHeader.id - 1)
                    val entryPos = chunkBegin + typeHeader.entriesStart - typeHeader.headerSize
                    Buffers.position(buffer, entryPos)
                    val b = buffer.slice()
                    b.order(byteOrder)
                    type.buffer = b
                    type.keyStringPool = resourcePackage.keyStringPool
                    type.offsets = offsets
                    type.stringPool = stringPool
                    resourcePackage.addType(type)
                    locales.add(type.locale)
                    Buffers.position(buffer, chunkBegin + typeHeader.bodySize)
                }

                // 512
                ChunkType.TABLE_PACKAGE.toShort() -> {
                    // another package. we should read next package here
                    pair.setRight((chunkHeader as PackageHeader))
                    break@outer
                }

                // 515
                ChunkType.TABLE_LIBRARY.toShort() -> {
                    // read entries
                    val libraryHeader = chunkHeader as LibraryHeader
                    var i: Long = 0
                    while (i < libraryHeader.count) {
                        val packageId = buffer.int
                        val name = Buffers.readZeroTerminatedString(buffer, 128)
                        val entry = LibraryEntry(packageId, name)
                        //TODO: now just skip it..
                        i++
                    }
                    Buffers.position(buffer, chunkBegin + chunkHeader.bodySize)
                }

                // 0
                ChunkType.NULL.toShort() -> {
                    // Buffers.position(buffer, chunkBegin + chunkHeader.getBodySize());
                    Buffers.position(buffer, (buffer.position() + buffer.remaining()).toLong())
                }

                else -> throw ParserException("unexpected chunk type: 0x" + chunkHeader.chunkType)
            }
        }
        return pair
    }

    private fun readChunkHeader(): ChunkHeader {
        val begin = buffer.position().toLong()
        val chunkType = Buffers.readUShort(buffer)
        val headerSize = Buffers.readUShort(buffer)
        val chunkSize = Buffers.readUInt(buffer)
        Log.d(
            "readChunkHeader",
            "begin:$begin chunkType:$chunkType headerSize:$headerSize chunkSize:$chunkSize"
        )
        return when (chunkType) {
            // 2
            ChunkType.TABLE -> {
                val resourceTableHeader = ResourceTableHeader(headerSize, chunkSize, buffer)
                Buffers.position(buffer, begin + headerSize)
                resourceTableHeader
            }

            // 1
            ChunkType.STRING_POOL -> {
                val stringPoolHeader = StringPoolHeader(headerSize, chunkSize, buffer)
                Buffers.position(buffer, begin + headerSize)
                stringPoolHeader
            }

            // 512
            ChunkType.TABLE_PACKAGE -> {
                val packageHeader = PackageHeader(headerSize, chunkSize, buffer)
                Buffers.position(buffer, begin + headerSize)
                packageHeader
            }

            // 514
            ChunkType.TABLE_TYPE_SPEC -> {
                val typeSpecHeader = TypeSpecHeader(headerSize, chunkSize, buffer)
                Buffers.position(buffer, begin + headerSize)
                typeSpecHeader
            }

            // 513
            ChunkType.TABLE_TYPE -> {
                val typeHeader = TypeHeader(headerSize, chunkSize, buffer)
                Buffers.position(buffer, begin + headerSize)
                typeHeader
            }

            // 515
            ChunkType.TABLE_LIBRARY -> {
                //DynamicRefTable
                val libraryHeader = LibraryHeader(headerSize, chunkSize, buffer)
                Buffers.position(buffer, begin + headerSize)
                libraryHeader
            }

            // 516 0
            ChunkType.UNKNOWN_YET, ChunkType.NULL -> {
                Buffers.positionInt(buffer, (begin + headerSize).toInt())
                NullHeader(headerSize, chunkSize)
            }

            else -> throw ParserException(
                "Unexpected chunk Type: 0x" + Integer.toHexString(
                    chunkType
                )
            )
        }
    }
}