package com.libertycat.kmp.demo.netwrok

//import io.ktor.client.plugins.logging.LogLevel
//import io.ktor.client.plugins.logging.Logger
//import io.ktor.client.plugins.logging.Logging
import com.libertycat.kmp.demo.beans.NetWorkResult
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
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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
//            logger = object : Logger {
//                override fun log(message: String) {
//                    println("KtorLogger:$message ")
//                }
//            }
//            level = LogLevel.NONE
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
    val NRF_ASSET_DETAIL = "/api/v5/mktplace/nft/asset/detail"//

    //获取合集交易信息 此 API 用于获取 NFT 销售历史信息，如收藏地址，平台，价格，买家和卖家。
    val NRF_MARKETS_TRADES = "api/v5/mktplace/nft/markets/trades"

    //查询挂单# 该接口用于查询指定 NFT 的有效欧易 Seaport 协议挂单。
    val NRF_MARKETS_LISTINGS = "api/v5/mktplace/nft/markets/listings"

    fun okSign(getUrl: String, utcTimeStamp: String): String {
        val originSign = "$utcTimeStamp$GET_METHOD$getUrl"
        return createSignature(originSign, ok_api_secret_key)

    }


    /**
     * 获取历史交易记录
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
            println("网络访问异常：" + e.message)
        }

        return NetWorkResult(
            code = 444,
            msg = "成交查询网络访问异常："
        )

    }

    /**
     * 查询挂单信息
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
            println("网络访问异常：" + e.message)
        }
        return NetWorkResult(
            code = 444,
            msg = "上架查询网络访问异常："
        )
    }


    fun getChainParam() = "?chain=$polygon"

    fun getCollectionAddressParam() = "&collectionAddress=$collectionAddress"
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
        println("创建签名失败")
    }
    return ""
}

fun getUTCTime(): String {
    //当前本地时间Date  对应的  UTC时间String
    val date = Date()
    val tz = TimeZone.getTimeZone("UTC")
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    df.timeZone = tz //获取时区
    val nowAsISO = df.format(date)
    return nowAsISO
}

