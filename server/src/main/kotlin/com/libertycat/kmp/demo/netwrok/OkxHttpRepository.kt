package com.libertycat.kmp.demo.netwrok

import com.libertycat.kmp.demo.beans.NetWorkDataResult
import com.libertycat.kmp.demo.beans.NetWorkResult
import com.libertycat.kmp.demo.beans.NtfDetail
import com.libertycat.kmp.demo.beans.SalesCat
import com.libertycat.kmp.demo.beans.Trade
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.serialization.json.Json
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object OkxHttpRepository {
    val client = HttpClient(OkHttp) {
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

    }
    val polygon = "Polygon"
    val collectionAddress = "0x0030f47d6a73bc518cf18fe027ea91dd6b2b6003"
    val ok_api_key = "c1defb0e-7cae-44c0-83f4-0b3af6f3283d"
    val ok_api_secret_key = "3F1EAA43DF7D53BE572D91D3BAAA27BE"
    val ok_api_pass_phrase = "9.7u8PaA4@Yg62P"
    val GET_METHOD = "GET"
    val POST_METHOD = "POST"

    val BASE_URL = "www.okx.com"
    val NRF_ASSET_DETAIL = "api/v5/mktplace/nft/asset/detail"//

    //Obtain collection transaction information. This API is used to retrieve NFT sales history information, including collection address, platform, price, buyer and seller.
    val NRF_MARKETS_TRADES = "api/v5/mktplace/nft/markets/trades"

    //Query Order # This interface is used to query the valid Seaport Protocol orders of the specified NFT.
    val NRF_MARKETS_LISTINGS = "api/v5/mktplace/nft/markets/listings"


    fun okSign(getUrl: String, utcTimeStamp: String): String {
        val originSign = "$utcTimeStamp$GET_METHOD$getUrl"
        return createSignature(originSign, ok_api_secret_key)

    }


    /**
     * queryTradesHistory
     */
    suspend fun queryTradesHistory(): NetWorkResult<Trade> {
        try {
            val tradesHistoryUrl = "/$NRF_MARKETS_TRADES${getChainParam()}${getCollectionAddressParam()}"
            val utcTimeStamp = getUTCTime()
//        val utcTimeStamp = "2024-10-23T02:13:01.933Z"
            val okSign = okSign(tradesHistoryUrl, utcTimeStamp)

            val httpResponse: HttpResponse = client.get {

                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path(NRF_MARKETS_TRADES)
                    parameters.append("chain", "Polygon")
                    parameters.append("collectionAddress", "0x0030f47d6a73bc518cf18fe027ea91dd6b2b6003")
                }
                header("OK-ACCESS-KEY", ok_api_key)
                header("OK-ACCESS-SIGN", okSign)
                header("OK-ACCESS-TIMESTAMP", utcTimeStamp)
                header("OK-ACCESS-PASSPHRASE", ok_api_pass_phrase)

                contentType(ContentType.Application.Json)
            }

            val stringBody: NetWorkResult<Trade> = httpResponse.body()
//        println("stringBody = $stringBody")
            return stringBody
        } catch (e: Throwable) {
            e.printStackTrace()
            println("Net error：" + e.message)
        }

        return NetWorkResult(
            code = 444,
            msg = "Net error："
        )

    }

    /**
     * queryOnSalesList
     */
    suspend fun queryOnSalesList(): NetWorkResult<SalesCat> {
        try {
            val tradesHistoryUrl = "/$NRF_MARKETS_LISTINGS${getChainParam()}${getCollectionAddressParam()}"
            val utcTimeStamp = getUTCTime()
//        val utcTimeStamp = "2024-10-23T02:13:01.933Z"
            val okSign = okSign(tradesHistoryUrl, utcTimeStamp)

            val httpResponse: HttpResponse = client.get {

                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path(NRF_MARKETS_LISTINGS)
                    parameters.append("chain", "Polygon")
                    parameters.append("collectionAddress", "0x0030f47d6a73bc518cf18fe027ea91dd6b2b6003")
                }
                header("OK-ACCESS-KEY", ok_api_key)
                header("OK-ACCESS-SIGN", okSign)
                header("OK-ACCESS-TIMESTAMP", utcTimeStamp)
                header("OK-ACCESS-PASSPHRASE", ok_api_pass_phrase)

                contentType(ContentType.Application.Json)
            }

            val stringBody: NetWorkResult<SalesCat> = httpResponse.body()
//        println("SaleCats = $stringBody")
//        client.close()
            return stringBody
        } catch (e: Throwable) {
            e.printStackTrace()
            println("Net error：" + e.message)
        }
        return NetWorkResult(
            code = 444,
            msg = "Net error："
        )
    }

    /**
     * queryNtfInfo
     */
    suspend fun queryNtfInfo(tokenId: String): NetWorkDataResult<NtfDetail> {
        try {
            val tradesHistoryUrl =
                "/$NRF_ASSET_DETAIL${getChainParam()}${getContractAddressParam()}${getTokenIdParam(tokenId)}"
            val utcTimeStamp = getUTCTime()
//        val utcTimeStamp = "2024-10-23T02:13:01.933Z"
            val okSign = okSign(tradesHistoryUrl, utcTimeStamp)

            val httpResponse: HttpResponse = client.get {

                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path(NRF_ASSET_DETAIL)
                    parameters.append("chain", "Polygon")
                    parameters.append("contractAddress", "0x0030f47d6a73bc518cf18fe027ea91dd6b2b6003")
                    parameters.append("tokenId", tokenId)
                }
                header("OK-ACCESS-KEY", ok_api_key)
                header("OK-ACCESS-SIGN", okSign)
                header("OK-ACCESS-TIMESTAMP", utcTimeStamp)
                header("OK-ACCESS-PASSPHRASE", ok_api_pass_phrase)

                contentType(ContentType.Application.Json)
            }

            val stringBody: NetWorkDataResult<NtfDetail> = httpResponse.body()
            println("SaleCats = $stringBody")
//        client.close()
            return stringBody
        } catch (e: Throwable) {
            e.printStackTrace()
            println("Net error：" + e.message)
        }
        return return NetWorkDataResult(
            code = 444,
            msg = "Net error："
        )
    }

    fun getChainParam() = "?chain=$polygon"
    fun getCollectionAddressParam() = "&collectionAddress=$collectionAddress"
    fun getContractAddressParam() = "&contractAddress=$collectionAddress"
    fun getTokenIdParam(tokenId: String) = "&tokenId=$tokenId"

    fun getRootPath(): String {
        return System.getProperty("user.dir");
    }

    suspend fun downloadImage(url: String, tokenId: String): String {
        println("Start Download picture:$url")
        try {
            val root = File(getRootPath() + "/cats/images")
            if (!root.exists()) {
                root.mkdirs()
            }
            val file = File(root.absolutePath, "${tokenId}.png")
            if (file.exists()) {
                return file.absolutePath
            }
            client.get(url).bodyAsChannel().copyAndClose(file.writeChannel())
            println("Download Success")
            return file.absolutePath
        } catch (e: Throwable) {
            e.printStackTrace()
            println("Download failure:${e.message}")
        }
        return ""
    }
}


@OptIn(ExperimentalEncodingApi::class)
fun createSignature(data: String, key: String): String {
//    println("createSignature data = $data key = $key")
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)

    try {
        return Base64.encode(sha256Hmac.doFinal(data.toByteArray()))
    } catch (e: Throwable) {
        e.printStackTrace()
        println("Failed to create signature")
    }
    return ""
}

fun getUTCTime(): String {
    val date = Date()
    val tz = TimeZone.getTimeZone("UTC")
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    df.timeZone = tz //get time zone
    val nowAsISO = df.format(date)
    return nowAsISO
}

