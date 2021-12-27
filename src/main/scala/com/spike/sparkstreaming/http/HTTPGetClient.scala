package com.spike.sparkstreaming.http

import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.IOException
import scala.util.{Try, Success, Failure}
import scala.util.control.Exception.Finally

class HTTPGetClient extends HTTPClient {

  def get(uri: String): String = {

    try {

      val request = new HttpGet(uri)

      // add request headers
      /*request.addHeader("custom-key", "")
      request.addHeader(HttpHeaders.USER_AGENT, "")*/

      val response = httpClient.execute(request)
      // Get HttpResponse Status
      println(response.getProtocolVersion()); // HTTP/1.1
      println(response.getStatusLine().getStatusCode()); // 200
      println(response.getStatusLine().getReasonPhrase()); // OK
      println(response.getStatusLine().toString()); // HTTP/1.1 200 OK

      val entity: Option[HttpEntity] = Option(response.getEntity())
      val respData = entity match {
        case Some(value) =>
          val json: String = EntityUtils.toString(entity.get)
          System.out.println(json)
          json
        case None =>
          ""
      }
      response.close
      respData
    }

  }

}