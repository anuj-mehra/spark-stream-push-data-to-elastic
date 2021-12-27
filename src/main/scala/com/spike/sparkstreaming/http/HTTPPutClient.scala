package com.spike.sparkstreaming.http

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPut
import org.apache.http.util.EntityUtils
import org.apache.http.entity.StringEntity

class HTTPPutClient extends HTTPClient{

  def put(uri: String, jsonMsg: String): String = {

    try {

      val request = new HttpPut(uri)
      request.setHeader("Content-Type", "application/json")
      request.setEntity(new StringEntity(jsonMsg))

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
