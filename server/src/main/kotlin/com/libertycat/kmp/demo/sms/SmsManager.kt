// This file is auto-generated, don't edit it. Thanks.
package com.libertycat.kmp.demo.sms

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest
import com.libertycat.kmp.demo.beans.CatAttribute
import com.libertycat.kmp.demo.beans.SalesCat
import com.libertycat.kmp.demo.beans.Trade
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.libertycat.kmp.demo.smsReceivers
import darabonba.core.client.ClientOverrideConfiguration


/**
 * 短信接收人
 */
val smsReceivers = listOf<String>(
    "133********",
)

object SmsManager {

    suspend fun sendNewTradesSms(trades: List<Trade>) {
        trades.forEach { trade ->
            sendOneNewTradesSms(trade)
        }

    }

    suspend fun sendOneNewTradesSms(trade: Trade): String {
        val smsResult = StringBuilder()
        smsReceivers.forEach { phone ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("tokenId", trade.tokenId)
//                jsonObject.addProperty("price", "${trade.realPrice()}-${trade.currency()}")
            jsonObject.addProperty("price", trade.realPrice())
            jsonObject.addProperty("from", "**${trade.from.takeLast(4)}")
            jsonObject.addProperty("to", "**${trade.to.takeLast(4)}")
            try {
                val result = sendSms(phone, "SMS_474845447", jsonObject.toString())
                smsResult.append(result)
            } catch (e: Throwable) {
                e.printStackTrace()
                smsResult.append(e.message)

            }
        }
        return smsResult.toString()
    }

    suspend fun sendNewOnSalesCatSms(salesCats: List<SalesCat>) {
        salesCats.forEach { salesCat ->
            sendOneNewOnSalesCatSms(salesCat)
        }

    }

    suspend fun sendOneNewOnSalesCatSms(salesCat: SalesCat): String {
        val smsResult = StringBuilder()
        smsReceivers.forEach { phone ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("tokenId", salesCat.tokenId)
            jsonObject.addProperty("price", salesCat.realPrice())
            try {
                val result = sendSms(phone, "SMS_475010440", jsonObject.toString())
                smsResult.append(result)
            } catch (e: Throwable) {
                e.printStackTrace()
                smsResult.append(e.message)
            }
        }
        return smsResult.toString()

    }

    fun sendSms(phoneNumber: String?, templateName: String?, jsonSmsParams: String?): String {
        println("phoneNumber = $phoneNumber templateName = $templateName jsonSmsParams = $jsonSmsParams")
        val provider = StaticCredentialProvider.create(
            Credential.builder() // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId("********")
                .accessKeySecret("********") //.secur
                .build()
        )

        // Configure the Client
        val client =
            AsyncClient.builder() //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider) //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                    ClientOverrideConfiguration.create() // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
                        .setEndpointOverride("dysmsapi.aliyuncs.com") //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build()

        // Parameter settings for API request
        val sendSmsRequest = SendSmsRequest.builder()
            .phoneNumbers(phoneNumber)
            .signName("盯盘喵")
            .templateCode(templateName)
            .templateParam(jsonSmsParams) // Request-level configuration rewrite, can set Http request parameters, etc.
            // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
            .build()

        // Asynchronously get the return value of the API request
        val response = client.sendSms(sendSmsRequest)
        // Synchronously get the return value of the API request
        val resp = response.get()
        val resultJson = Gson().toJson(resp)
        println(resultJson)

        client.close()
        return resultJson
    }

    suspend fun testTradesSms(): String {
        val trade = Trade(
            amount = 1,
            chain = "Polygon",
            collectionAddress = "0x0030f47d6a73bc518cf18fe027ea91dd6b2b6003",
            currencyAddress = "0x3c499c542cEF5E3811e1192ce70d8cC03d5c3359",
            from = "0x082d3fc3de3d56d0be914f64df1306ff691b5cb5",
            platform = "OKX",
            price = "28572.0",
            timestamp = 1729817019,
            tokenId = "950",
            txHash = "0x70aa4cd332f1e09a593572c678abf83d444557cf4f948502e3d833da3d3273ba",
            to = "0x56b6e3f730f135a4fd5085170dc7c06288156834"
        )
        return sendOneNewTradesSms(trade)
    }


    suspend fun testOnSalesCatSms(): String {
        val salesCat = SalesCat(
            amount = 1,
            attributes = CatAttribute(
                furColor = "Orange",
                eyes = "Alert",
                background = "Pink To Orange Gradient",
                face = "Yum",
                gear = ""
            ),
            currencyAddress = "0x3c499c542cEF5E3811e1192ce70d8cC03d5c3359",
            createTime = 1729816786,
            updateTime = 1729816786,
            description = "",
            price = "29000000000",
            tokenId = "5446",
            status = "active",
            orderType = "BuyNow",
            image = "https://static.coinall.ltd/cdn/nft/files/d69dbf6f-bf5c-412f-8d92-7c2f7912d1bd.webp/type=detail"
        )
        return sendOneNewOnSalesCatSms(salesCat)
    }


}

