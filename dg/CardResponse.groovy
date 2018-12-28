package dg

// Holds data for card response
class CardResponse {
  def recordId = 0
  def guid = ''
  def decision = ''
  def ref = ''
  def txnId = ''  
  def status = ''
  def errorCode = ''
  def message = ''
  def modificationDate = ''

  CardResponse( recordId, decision, guid ) {
    this.recordId = recordId
    this.decision = decision
    this.guid = guid
  }

  String toString() {
    if( decision == 'SUCCESS' )
      return guid + ' SUCCESS id: ' + txnId
    else
      return guid + ' FAILED id: ' + txnId + ' Error code: ' + errorCode + ' - ' + message
  }
}