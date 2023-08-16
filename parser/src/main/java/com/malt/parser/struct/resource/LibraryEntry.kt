package com.malt.parser.struct.resource

/**
 * Library chunk entry
 *
 * @author malt
 */
class LibraryEntry(packageId: Int, name: String) {
    /**
     * uint32. The package-id this shared library was assigned at build time.
     */
    private val packageId: Int

    /**
     * The package name of the shared library. \0 terminated. max 128
     */
    private val name: String

    init {
        this.packageId = packageId;
        this.name = name;
    }
}