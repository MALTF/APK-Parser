package com.malt.parser.cert.asn1

/**
 * @author maliang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Asn1Class(val type: Asn1Type)