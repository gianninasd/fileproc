import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.PatternLayout
import static ch.qos.logback.classic.Level.INFO
 
scan("30 seconds")

def LOG_PATH = "logs"

appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyy-MM-dd HH:mm:ss} GUID=%X{guid} %-5level %logger{0} - %msg%n"
    }
}
appender("File-Appender", FileAppender) {
    file = "${LOG_PATH}/fileProc.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyy-MM-dd HH:mm:ss} GUID=%X{guid} %-5level %logger{0} - %msg%n"
    }
}

root(INFO, ["Console-Appender","File-Appender"])