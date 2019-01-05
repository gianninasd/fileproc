package dg

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import dg.CardResponse
import dg.CardJsonUtils

// Client class used to call an external REST API for transaction processing
class CardClient {
  def cardUrl = ''
  def apiKey = ''

  // sends a purchase request to a remote REST API
  def purchase( cardRequest ) {
    def http = new HTTPBuilder( cardUrl )
    http.setHeaders([Authorization: "Basic " + apiKey])

    def result = null

    try {
      http.request( POST ) {
        uri.path = "/cardpayments/v1/accounts/$cardRequest.accountId/auths"
        requestContentType = JSON
        body = CardJsonUtils.createJson( cardRequest )

        response.success = { resp, json ->
          result = new CardResponse(cardRequest.recordId, 'SUCCESS', cardRequest.ref)
          result.txnId = json.id
          result.ref = json.merchantRefNum
          result.status = json.status
        }

        response.failure = { resp, json ->
          result = new CardResponse(cardRequest.recordId, 'FAILED', cardRequest.ref)
          result.txnId = json.id
          result.ref = json.merchantRefNum
          result.errorCode = json.error.code
          result.message = json.error.message
        }
      }
    }
    catch( Exception ex ) {
      result = new CardResponse(cardRequest.recordId, 'ERROR', cardRequest.ref)
    }

    return result
  }
}