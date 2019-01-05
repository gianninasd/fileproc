package dg

import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable

import dg.CardClient
import dg.LineParser
import dg.RecordDAO

// class that will execute within a thread pool and calls a remote API
class ProcessRequest implements Callable {
  def logger = LoggerFactory.getLogger('ProcessRequest')

  def recordDAO
  def config
  def recordId
  def line

  def ProcessRequest( recordDAO, config, recordId, line ) {
    this.recordDAO = recordDAO
    this.config = config
    this.recordId = recordId
    this.line = line
  }

  def call() {
    def guid = UUID.randomUUID().toString()
    def client = new CardClient(
      cardUrl: config.url, 
      apiUser: config.apiUser, 
      apiPass: config.apiPass)

    try {
      def parser = new LineParser()
      def lineReq = parser.parse(recordId, line)
      lineReq.guid = guid
      lineReq.ref = guid // we do this to make sure records work due to test data, not needed in PROD

      logger.info "Sending reference ${lineReq.ref} with amount ${lineReq.amount}"
      recordDAO.updateSent(lineReq)

      return client.purchase( lineReq )
    }
    catch( Exception ex ) {
      logger.warn "$guid with $recordId - Line processing failed: $ex"
      def resp = new CardResponse(recordId, 'ERROR', guid)
      resp.message = ex.getMessage()
      return resp
    }
  }
}