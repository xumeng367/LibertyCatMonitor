package com.libertycat.kmp.demo.component

import com.libertycat.kmp.demo.beans.NetWorkResult
import com.libertycat.kmp.demo.netwrok.OkxHttpRepository
import com.libertycat.kmp.demo.beans.SalesCat
import com.libertycat.kmp.demo.beans.Trade
import com.libertycat.kmp.demo.mail.MailManager
import com.libertycat.kmp.demo.sms.SmsManager
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.collections.filter
import kotlin.collections.isNotEmpty
import kotlin.collections.maxBy
import kotlin.collections.sortedByDescending

@Component
class CatMonitorTasks {
    @Autowired
    private lateinit var mailManager: MailManager

    var lastTradesHistory: NetWorkResult<Trade>? = null
    var lastSalesCatList: NetWorkResult<SalesCat>? = null

    var tradeQueryLog = ""
    var starQueryTradesInit = false;
    var starQueryOnSaLESInit = false;
    fun startQueryTradesHistoryTask() {
        if (starQueryTradesInit) {
            println("The inquiry trade service has been activated!!")
            return
        }
        starQueryTradesInit = true
        val scope = CoroutineScope(Dispatchers.Default)
        var count = 0
        scope.launch {
            while (true) {
                delay(2000) // Execute once every 5 seconds
                println()
                val tradesHistory = OkxHttpRepository.queryTradesHistory()
                val lastTradesData = lastTradesHistory?.data?.data
                val currentTradesData = tradesHistory?.data?.data
                if (lastTradesData != null && currentTradesData != null && lastTradesData.isNotEmpty() && currentTradesData.isNotEmpty()) {
                    val lastLatestTrade = lastTradesData.maxBy { it.timestamp }
                    val newTrades = currentTradesData.filter { it.timestamp > lastLatestTrade.timestamp }
//                println("执行成交信息查询...$count lastLatestTrade tokenId = ${lastLatestTrade.tokenId} = realPrice = ${lastLatestTrade.realPrice()} ，新发现：${newTrades.size}")
                    tradeQueryLog =
                        "count = $count, lastLatestTrade tokenId = ${lastLatestTrade.tokenId} = realPrice = ${lastLatestTrade.realPrice()} ，新发现：${newTrades.size}"
                    if (newTrades.isNotEmpty()) {
                        println("Discover a new record of trade：" + newTrades.joinToString())
//                        SmsManager.sendNewTradesSms(newTrades)
                        mailManager.sendTradeMails(newTrades)
                    } else {
//                    println("历史最新：$lastLatestTrade")
                    }
                }
                count++
                lastTradesHistory = tradesHistory;
            }
        }
    }

    fun startQueryOnSalesListTask() {
        if (starQueryOnSaLESInit) {
            println("The listing service has been activated.")
            return
        }
        starQueryOnSaLESInit = true
        val scope = CoroutineScope(Dispatchers.Default)
        var count = 0
        scope.launch {
            while (true) {
                delay(3000) // Execute once every 5 seconds
                println()
                val currentOnSalesCatList = OkxHttpRepository.queryOnSalesList()
                val lastOnSalesCatData = lastSalesCatList?.data?.data
                if (lastOnSalesCatData != null) {
                    lastOnSalesCatData.sortedByDescending { it.updateTime }
                }
                val currentOnSalesData = currentOnSalesCatList?.data?.data
                if (lastOnSalesCatData != null && currentOnSalesData != null && lastOnSalesCatData.isNotEmpty() && currentOnSalesData.isNotEmpty()) {
                    val lastLatestOnSaleCat = lastOnSalesCatData.maxBy { it.updateTime }
                    val newTrades = currentOnSalesData.filter { it.updateTime > lastLatestOnSaleCat.updateTime }
                    if (newTrades.isNotEmpty()) {
                        println("new Cat is here：")
                        println("new Cat info：" + newTrades.joinToString())
//                        SmsManager.sendNewOnSalesCatSms(newTrades)
                        mailManager.sendNewOnSalesCatEmails(newTrades)

                    } else {
//                    println("latest info：$lastLatestTrade")
                    }
                }
                count++
                lastSalesCatList = currentOnSalesCatList;
            }
        }
    }


    @PostConstruct
    fun runAfterStartUp() {
        println("Spring started, start service")
        startQueryTradesHistoryTask()
//        startQueryOnSalesListTask()
        println("path = ${OkxHttpRepository.getRootPath()}")
    }


    suspend fun testSendTradesEmails() {
        val data = lastTradesHistory?.data?.data
        if (data != null && data.size > 0) {
            val lastLatestTrade = data.maxBy { it.timestamp }
            mailManager.sendTradeMail(lastLatestTrade)
        }
    }

    suspend fun testSendOnSalesEmails() {
        val data = lastSalesCatList?.data?.data
        if (data != null && data.size > 0) {
            val lastLatestTrade = data.maxBy { it.updateTime }
            OkxHttpRepository.downloadImage(lastLatestTrade.image, lastLatestTrade.tokenId)
            mailManager.sendNewOnSalesCatEmail(lastLatestTrade)
        }
    }

}
