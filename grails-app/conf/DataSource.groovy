grails {
  mongo {
    host = "localhost"
    port = 27017
    username = ""
    password = ""
    databaseName = "wenergy"
    options {
      autoConnectRetry = true
      connectTimeout = 300
    }
  }
}
