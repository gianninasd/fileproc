package dg

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

// utils to encrypt/decrypt a string value
class CryptoUtil {

  static ALGO = "AES"
  static TRANSFORM_ALGO = "AES/CBC/PKCS5Padding"
  static ENCODING = "UTF-8"
  static MD_ALGO = "SHA-1"

  String secretKey

  // transforms the secret key into bytes for future operations
  def prepareSecret( String secret ) {
    byte[] secretAsBytes = secret.getBytes(ENCODING)
    def sha = MessageDigest.getInstance(MD_ALGO)
    secretAsBytes = sha.digest(secretAsBytes)
    return Arrays.copyOf(secretAsBytes, 16) // only first 16 bytes since its AES w/ CBC
  }
  
  // encrypts the given value, returning a scrambled string
  def encrypt( String value ) {
    def secretAsBytes = prepareSecret(this.secretKey)
    SecretKeySpec ss = new SecretKeySpec(secretAsBytes, ALGO)
    IvParameterSpec ivspec = new IvParameterSpec(secretAsBytes)

    Cipher cipher = Cipher.getInstance(TRANSFORM_ALGO)
    cipher.init(Cipher.ENCRYPT_MODE, ss, ivspec)
    def cipherBytes = c.doFinal(value.getBytes(ENCODING))
    return Base64.getEncoder().encodeToString(cipherBytes)
  }

  // decrypts the given value, returning the original string
  def decrypt( String value ) {
    def secretAsBytes = prepareSecret(this.secretKey)
    SecretKeySpec ss = new SecretKeySpec(secretAsBytes, ALGO)
    IvParameterSpec ivspec = new IvParameterSpec(secretAsBytes)

    Cipher cipher = Cipher.getInstance(TRANSFORM_ALGO)
    cipher.init(Cipher.DECRYPT_MODE, ss, ivspec)
    byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(value))
    return new String(bytes)
  }
}