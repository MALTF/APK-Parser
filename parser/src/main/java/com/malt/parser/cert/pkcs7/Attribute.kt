package com.malt.parser.cert.pkcs7

import com.malt.parser.cert.asn1.Asn1Class
import com.malt.parser.cert.asn1.Asn1Field
import com.malt.parser.cert.asn1.Asn1OpaqueObject
import com.malt.parser.cert.asn1.Asn1Type

/**
 * PKCS #7 `Attribute` as specified in RFC 5652.
 */
@Asn1Class(type = Asn1Type.SEQUENCE)
class Attribute {
    @Asn1Field(index = 0, type = Asn1Type.OBJECT_IDENTIFIER)
    var attrType: String? = null

    @Asn1Field(index = 1, type = Asn1Type.SET_OF)
    var attrValues: List<Asn1OpaqueObject>? = null
}