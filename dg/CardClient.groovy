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

  // sends a purchase request to a remote REST API
  def purchase( cardRequest ) {
    def http = new HTTPBuilder( cardUrl )
    http.auth.basic apiUser, apiPass

    // constructs a collection that will turn into a JSON representation
    def builder = new JsonBuilder()
    builder {
      merchantRefNum cardRequest.ref
      amount cardRequest.amount
      settleWithAuth cardRequest.txnType == "P"? true: false
      card {
        cardNum cardRequest.cardNbr
        cardExpiry {
          month cardRequest.cardExpMth
          year cardRequest.cardExpYear
        }
        cvv cardRequest.cvv
      }
      profile {
        firstName cardRequest.firstName
        lastName cardRequest.lastName
        email cardRequest.email
      }
      billingDetails {
        street cardRequest.addr1
        street2 cardRequest.addr2
        city cardRequest.city
        state cardRequest.province
        country cardRequest.country
        zip cardRequest.zipCode
        phone cardRequest.phone
      }
    }

    def result = null

    try {
      http.request( POST ) {
        uri.path = "/cardpayments/v1/accounts/$cardRequest.accountId/auths"
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