package dg

// thrown when the minimum number of tokens is missing
class MissingTokensException extends Exception {

  def MissingTokensException() {
    super()
  }

  def MissingTokensException( String s ) {
    super( s )
  }

}