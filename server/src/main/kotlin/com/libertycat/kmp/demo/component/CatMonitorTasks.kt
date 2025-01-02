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

    var lastTradesHistory: NetWorkResult<Trade>? = null//上次交易记录
    var lastSalesCatList: NetWorkResult<SalesCat>? = null//上次交易记录

    var tradeQueryLog = ""
    var starQueryTradesInit = false;
    var starQueryOnSaLESInit = false;
    fun startQueryTradesHistoryTask() {
        if (starQueryTradesInit) {
            println("查询成交服务已启动")
            return
        }
        starQueryTradesInit = true
        val scope = CoroutineScope(Dispatchers.Default)
        var count = 0
        scope.launch {
            while (true) {
                delay(2000) // 每5秒执行一次
                println()
                // 在这里放置你的协程任务代码
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
                        println("发现新成交记录：" + newTrades.joinToString())
                        SmsManager.sendNewTradesSms(newTrades)
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
            println("查询上架服务已启动")
            return
        }
        starQueryOnSaLESInit = true
        val scope = CoroutineScope(Dispatchers.Default)
        var count = 0
        scope.launch {
            while (true) {
                delay(3000) // 每5秒执行一次
                println()
                // 在这里放置你的协程任务代码
                val currentOnSalesCatList = OkxHttpRepository.queryOnSalesList()
                val lastOnSalesCatData = lastSalesCatList?.data?.data
                if (lastOnSalesCatData != null) {
                    lastOnSalesCatData.sortedByDescending { it.updateTime }
                }
                val currentOnSalesData = currentOnSalesCatList?.data?.data
                if (lastOnSalesCatData != null && currentOnSalesData != null && lastOnSalesCatData.isNotEmpty() && currentOnSalesData.isNotEmpty()) {
                    val lastLatestOnSaleCat = lastOnSalesCatData.maxBy { it.updateTime }
                    val newTrades = currentOnSalesData.filter { it.updateTime > lastLatestOnSaleCat.updateTime }
//                    println("执行上架信息查询...$count lastLatestOnSaleCat = isSellOrder = ${lastLatestOnSaleCat.isSellOrder()} currency = ${lastLatestOnSaleCat.currency()} realPrice = ${lastLatestOnSaleCat.realPrice()} ，新发现：${newTrades.size}")
//                println("lastOnSalesCatData size = ${lastOnSalesCatData.size} ,  currentOnSalesData size = ${currentOnSalesData.size} ,newTrades = ${newTrades.size}")
//                println("newTrades: ${newTrades.joinToString()}")
                    if (newTrades.isNotEmpty()) {
                        println("有新猫猫架了：")
                        println("上架信息动态：" + newTrades.joinToString())
//                        SmsManager.sendNewOnSalesCatSms(newTrades)
                        mailManager.sendNewOnSalesCatEmails(newTrades)

                    } else {
//                    println("历史最新：$lastLatestTrade")
                    }
                }
                count++
                lastSalesCatList = currentOnSalesCatList;
            }
        }
    }


    @PostConstruct
    fun runAfterStartUp() {
        println("Spring启动后，执行成交查询服务")
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
