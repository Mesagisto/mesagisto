package org.mesagisto.client


import org.rfc8452.aead.AesGcmSiv
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.concurrent.getOrSet

object Cipher {
  private lateinit var key: ByteArray
  private lateinit var nonce: ByteArray
  lateinit var rawKey: String
  private val secureRandom: SecureRandom by lazy { SecureRandom() }

  private val inner: ThreadLocal<AesGcmSiv> = ThreadLocal()
  fun newNonce(): ByteArray {
    val nonce = ByteArray(12)
    secureRandom.nextBytes(nonce)
    return nonce
  }
  fun init(key: String) {
    rawKey = key
    val digest = MessageDigest.getInstance("SHA-256")
    val key256 = digest.digest(key.toByteArray())
    Cipher.key = key256
    Cipher.nonce = key256.slice(0..11).toByteArray()
  }

  fun encrypt(plaintext: ByteArray): ByteArray {
    return inner.getOrSet { AesGcmSiv(key) }.seal(plaintext, byteArrayOf(), nonce)
  }

  fun decrypt(ciphertext: ByteArray): ByteArray {
    return inner.getOrSet { AesGcmSiv(key) }.open(ciphertext, byteArrayOf(), nonce)
  }
  fun uniqueAddress(address: String): String {
    return "$address$rawKey"
  }
}
