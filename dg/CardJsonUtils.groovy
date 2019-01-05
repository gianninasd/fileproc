package dg

import groovy.json.JsonBuilder
import groovy.json.JsonOutput

// utility class for converting objects to their JSON representation
class CardJsonUtils {
  
  // constructs a collection that will turn into a JSON representation
  def static String createJson( cardRequest ) {
    def builder = new JsonBuilder()
    def data = builder.cc {
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

    if( cardRequest.previousCustomer == 'Y' ) {
      data.cc.recurring = 'RECURRING'
    }
    else if( cardRequest.previousCustomer == 'N' ) {
      data.cc.recurring = 'INITIAL'
    }

    def output = new JsonOutput()
    return output.toJson(data.cc)
  }
}