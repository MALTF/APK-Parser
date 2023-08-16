package com.malt.parser.bean

/**
 * Apk sign status.
 * @author malt
 * @noinspection AlibabaEnumConstantsMustHaveComment
 */
enum class ApkSignStatus {
    notSigned,  // invalid signing
    incorrect, signed
}