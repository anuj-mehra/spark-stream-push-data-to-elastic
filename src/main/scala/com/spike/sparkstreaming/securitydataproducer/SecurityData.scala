package com.spike.sparkstreaming.securitydataproducer

case class SecurityData(id: String,
                        cusip: String,
                        isin: String,
                        securityType: String,
                        value: Integer,
                        currency: String)


object SecurityDataJSONConverter {

  import com.google.gson.Gson

  def convertToJson(data: SecurityData): String = {

    val gson = new Gson
    val jsonString = gson.toJson(data)
    println(jsonString)
    jsonString
  }

  def convertFromJson(jsonString: String): SecurityData = {
    val gson = new Gson
    gson.fromJson(jsonString, classOf[SecurityData])
  }

}

object SecurityDataType extends Enumeration {

  type Main = Value

  // Assigning values
  val EQUITY = Value("equity").toString
  val CORP = Value("corp").toString
  val MUNI = Value("muni").toString
  val GOVT = Value("govt").toString

}




