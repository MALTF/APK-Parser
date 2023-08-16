package com.malt.parser.parser

import com.malt.parser.bean.CertificateMeta
import com.malt.parser.cert.asn1.Asn1BerParser
import com.malt.parser.cert.asn1.Asn1DecodingException
import com.malt.parser.cert.pkcs7.ContentInfo
import com.malt.parser.cert.pkcs7.Pkcs7Constants
import com.malt.parser.cert.pkcs7.SignedData
import com.malt.parser.utils.Buffers.readBytes
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * Parser certificate info using jsse.
 *
 * @author malt
 */
internal class JSSECertificateParser(data: ByteArray?) : CertificateParser(data) {
    @Throws(CertificateException::class)
    override fun parse(): List<CertificateMeta?> {
        val contentInfo: ContentInfo
        contentInfo = try {
            Asn1BerParser.parse(data?.let { ByteBuffer.wrap(it) }, ContentInfo::class.java)
        } catch (e: Asn1DecodingException) {
            throw CertificateException(e)
        }
        if (Pkcs7Constants.OID_SIGNED_DATA != contentInfo.contentType) {
            throw CertificateException("Unsupported ContentInfo.contentType: " + contentInfo.contentType)
        }
        val signedData: SignedData
        signedData = try {
            Asn1BerParser.parse(contentInfo.content?.encoded, SignedData::class.java)
        } catch (e: Asn1DecodingException) {
            throw CertificateException(e)
        }
        val encodedCertificates = signedData.certificates
        val certFactory = CertificateFactory.getInstance("X.509")
        val result: MutableList<X509Certificate?> = ArrayList(
            encodedCertificates!!.size
        )
        for (i in encodedCertificates.indices) {
            val encodedCertificate = encodedCertificates[i]
            val encodedForm: ByteArray = readBytes(encodedCertificate.encoded)
            val certificate = certFactory.generateCertificate(ByteArrayInputStream(encodedForm))
            result.add(certificate as X509Certificate)
        }
        return CertificateMetas.from(result)
    }
}