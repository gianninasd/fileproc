package dg

import java.security.MessageDigest
import dg.DupeFileException

// Service class for all file operations
class FileService {
  
  def secretKey

  def FileService( secretKey ) {
    this.secretKey = secretKey
  }

  // extracts only the filename without the extension
  def extractFileName( fullFileName ) {
    def idx = fullFileName.indexOf('.csv')
    return fullFileName[0..--idx]
  }

  // creates a file record in the DB
  // raises DupeFileException if file was previously uploaded in the last 24 hrs
  def create( workingDir, fileName ) {
    def fileHash = calculateHash(workingDir, fileName)
    //cnt = fileDAO.countInLast24hrs(fileHash)

    //fileId = fileDAO.create(fileName, fileHash)
    def cnt = 0
    if( cnt > 0 )
      raise DupeFileException()

    return 0
  }

  // generates the hash value of the file contents
  def calculateHash( workingDir, fileName ) {
    def file = new File(workingDir + "/" + fileName)
    def digest = MessageDigest.getInstance("SHA-1")
    def inputstream = file.newInputStream()
    def buffer = new byte[16384]
    def len

    while((len=inputstream.read(buffer)) > 0) {
      digest.update(buffer, 0, len)
    }
    def sha1sum = digest.digest()

    def result = ""
    for(byte b : sha1sum) {
      result += toHex(b)
    }
    return result
  }

  def hexChr(int b) {
    return Integer.toHexString(b & 0xF)
  }

  def toHex(int b) {
    return hexChr((b & 0xF0) >> 4) + hexChr(b & 0x0F)
  }
}