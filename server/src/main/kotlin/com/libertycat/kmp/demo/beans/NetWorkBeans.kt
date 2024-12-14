package com.libertycat.kmp.demo.beans

import com.libertycat.kmp.demo.web3.tokenAddressToCurrency
import kotlinx.serialization.*
import kotlin.text.replace

@Serializable
data class NetWorkResult<out T>(val code: Int, val msg: String, val data: Data<T>? = null)

@Serializable
data class NetWorkDataResult<out T>(val code: Int, val msg: String, val data: T? = null)

@Serializable
data class Data<out T>(val cursor: String, val data: List<T>)

@Serializable
data class Trade(
    val amount: Int,
    val chain: String,
    val collectionAddress: String,
    val currencyAddress: String,
    val from: String,
    val platform: String,
    val price: String,
    val timestamp: Long,
    val to: String,
    val tokenId: String,
    val txHash: String,
) {
    fun realPrice(): String {
        return price.replace("000000", "")
    }

    fun currency(): String {
        return tokenAddressToCurrency[currencyAddress] ?: ""
    }
}

@Serializable
data class SalesCat(
    val amount: Int,
    val attributes: CatAttribute,
    val createTime: Long,
    val updateTime: Long,
    val description: String,
    val price: String,
    val status: String,
    val tokenId: String,
    val orderType: String,
    val currencyAddress: String,
    val image: String
) {
    /**
     * 是否是买单
     */
    fun isSellOrder(): Boolean = "BuyNow" == orderType
    fun realPrice(): String {
        return price.replace("000000", "")
    }

    fun currency(): String {
        return tokenAddressToCurrency[currencyAddress] ?: ""
    }
}


@Serializable
data class NtfDetail(
    val tokenId: String,
    val image: String = ""
) {
}



@Serializable
data class CatAttribute(
    @SerialName("Fur Color") val furColor: String,
    @SerialName("Eyes") val eyes: String,
    @SerialName("Background") val background: String,
    @SerialName("Face") val face: String,
    @SerialName("Gear") val gear: String
)
