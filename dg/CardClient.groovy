package dg

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import dg.CardResponse

// Client class used to call an external REST API for transaction processing
class CardClient {
  def cardUrl = ''
  def apiUser = ''
  def apiPass = ''
  def accountId = ''

  // sends a purchase request to a remote REST API
  def purchase( cardRequest ) {
    def http = new HTTPBuilder( cardUrl )
    http.auth.basic apiUser, apiPass
    
    // constructs a collection that will turn into a JSON representation
    def builder = new JsonBuilder()
    builder {
      merchantRefNum cardRequest.ref
      amount cardRequest.amount
      settleWithAuth true
      card {
        cardNum cardRequest.cardNbr
        cardExpiry {
          month cardRequest.cardExpMth
          year cardRequest.cardExpYear
        }
      }
      profile {
        firstName cardRequest.firstName
        lastName cardRequest.lastName
        email cardRequest.email
      }
      billingDetails {
        zip cardRequest.zipCode
      }
    }

    def result = null

    try {
      http.request( POST ) {
        uri.path = "/cardpayments/v1/accounts/$accountId/auths"
        requestContentType = JSON
        body = builder.toString()

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