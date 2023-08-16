package com.malt.parser.struct

/**
 * String pool.
 *
 * @author malt
 */
class StringPool(poolSize: Int) {
    private val pool: Array<String?>

    init {
        pool = arrayOfNulls(poolSize)
    }

    operator fun get(idx: Int): String? {
        return pool[idx]
    }

    operator fun set(idx: Int, value: String?) {
        pool[idx] = value
    }
}