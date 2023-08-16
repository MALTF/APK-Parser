package com.malt.parser.parser

import android.util.Log
import com.malt.parser.struct.signingv2.ApkSigningBlock
import com.malt.parser.struct.signingv2.Digest
import com.malt.parser.struct.signingv2.Signature
import com.malt.parser.struct.signingv2.SignerBlock
import com.malt.parser.utils.Buffers
import com.malt.parser.utils.Buffers.readBytes
import com.malt.parser.utils.Unsigned
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * The Apk Sign Block V2 Parser.
 * @author malt
 *
 * {@link see https://source.android.com/security/apksigning/v2}
 */
class ApkSignBlockParser(data: ByteBuffer) {
    private val data: ByteBuffer

    @Throws(CertificateException::class)
    fun parse(): ApkSigningBlock {
        // sign block found, read pairs
        val signerBlocks: MutableList<SignerBlock> = ArrayList()
        while (data.remaining() >= 8) {
            val id = data.int
            val size = Unsigned.ensureUInt(id.toLong())
            Log.d("ApkSigningBlock", "id:$id size:$size remaining:${data.remaining()}")
            if (id == ApkSigningBlock.SIGNING_V2_ID) {
                val signingV2Buffer = Buffers.sliceAndSkip(data, size)
                Log.d("signingV2Buffer", "signingV2Buffer:${signingV2Buffer.hasRemaining()}")
                // now only care about apk signing v2 entry
                while (signingV2Buffer.hasRemaining()) {
                    val signerBlock = readSigningV2(signingV2Buffer)
                    signerBlocks.add(signerBlock)
                }
            } else {
                // just ignore now
                val index = data.position() + size
                Log.d("ApkSigningBlock", "index:$index")
                val  limit = data.limit()
                if ((index > limit) || (index < 0)) {
                    continue
                }
                Buffers.positionInt(data, index)
            }
        }
        return ApkSigningBlock(signerBlocks)
    }

    @Throws(CertificateException::class)
    private fun readSigningV2(buffer: ByteBuffer): SignerBlock {
        val nBuffer = readLenPrefixData(buffer)
        val signedData = readLenPrefixData(nBuffer)
        val digestsData = readLenPrefixData(signedData)
        val digests = readDigests(digestsData)
        val certificateData = readLenPrefixData(signedData)
        val certificates = readCertificates(certificateData)
        val attributesData = readLenPrefixData(signedData)
        readAttributes(attributesData)
        val signaturesData = readLenPrefixData(nBuffer)
        val signatures = readSignatures(signaturesData)
        val publicKeyData = readLenPrefixData(nBuffer)
        return SignerBlock(digests, certificates, signatures)
    }

    private fun readDigests(buffer: ByteBuffer): List<Digest> {
        val list: MutableList<Digest> = ArrayList()
        while (buffer.hasRemaining()) {
            val digestData = readLenPrefixData(buffer)
            val algorithmID = digestData.int
            val digest: ByteArray = readBytes(digestData)
            list.add(Digest(algorithmID, digest))
        }
        return list
    }

    @Throws(CertificateException::class)
    private fun readCertificates(buffer: ByteBuffer): List<X509Certificate> {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates: MutableList<X509Certificate> = ArrayList()
        while (buffer.hasRemaining()) {
            val certificateData = readLenPrefixData(buffer)
            val certificate = certificateFactory.generateCertificate(
                ByteArrayInputStream(readBytes(certificateData))
            )
            certificates.add(certificate as X509Certificate)
        }
        return certificates
    }

    private fun readAttributes(buffer: ByteBuffer) {
        while (buffer.hasRemaining()) {
            val attributeData = readLenPrefixData(buffer)
            val id = attributeData.int
        }
    }

    private fun readSignatures(buffer: ByteBuffer): List<Signature> {
        val signatures: MutableList<Signature> = ArrayList()
        while (buffer.hasRemaining()) {
            val signatureData = readLenPrefixData(buffer)
            val algorithmID = signatureData.int
            val signatureDataLen = Unsigned.ensureUInt(
                signatureData.int.toLong()
            )
            val signature = readBytes(signatureData, signatureDataLen)
            signatures.add(Signature(algorithmID, signature))
        }
        return signatures
    }

    private fun readLenPrefixData(buffer: ByteBuffer): ByteBuffer {
        val len = Unsigned.ensureUInt(buffer.int.toLong())
        return Buffers.sliceAndSkip(buffer, len)
    }

    init {
        this.data = data.order(ByteOrder.LITTLE_ENDIAN)
    }

    companion object {
        // 0x0101—RSASSA-PSS with SHA2-256 digest, SHA2-256 MGF1, 32 bytes of salt, trailer: 0xbc
        private const val PSS_SHA_256 = 0x0101

        // 0x0102—RSASSA-PSS with SHA2-512 digest, SHA2-512 MGF1, 64 bytes of salt, trailer: 0xbc
        private const val PSS_SHA_512 = 0x0102

        // 0x0103—RSASSA-PKCS1-v1_5 with SHA2-256 digest. This is for build systems which require deterministic signatures.
        private const val PKCS1_SHA_256 = 0x0103

        // 0x0104—RSASSA-PKCS1-v1_5 with SHA2-512 digest. This is for build systems which require deterministic signatures.
        private const val PKCS1_SHA_512 = 0x0104

        // 0x0201—ECDSA with SHA2-256 digest
        private const val ECDSA_SHA_256 = 0x0201

        // 0x0202—ECDSA with SHA2-512 digest
        private const val ECDSA_SHA_512 = 0x0202

        // 0x0301—DSA with SHA2-256 digest
        private const val DSA_SHA_256 = 0x0301
    }
}