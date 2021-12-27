package com.spike.sparkstreaming.http

import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}

object HTTPClient {

  val httpClient: CloseableHttpClient = HttpClients.createDefault
}

class HTTPClient {

  val httpClient = HTTPClient.httpClient
}
