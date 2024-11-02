package com.libertycat.kmp.demo.controller

import com.libertycat.kmp.demo.netwrok.OkxHttpRepository
import com.libertycat.kmp.demo.sms.SmsManager
import com.libertycat.kmp.demo.task.startQueryOnSalesListTask
import com.libertycat.kmp.demo.task.startQueryTradesHistoryTask
import com.libertycat.kmp.demo.task.tradeQueryLog
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CatRestController {

    @GetMapping("/")
    suspend fun home() = "Hello Liberty Cat!"

    @GetMapping("/startTrades")
    suspend fun startTrades(): String {
        startQueryTradesHistoryTask()
        println("启动成交查询服务")
        return "启动成交查询服务"
    }

    @GetMapping("/startListings")
    suspend fun startListings(): String {
        startQueryOnSalesListTask()
        println("启动上架查询服务")
        return "启动上架查询服务"
    }


    @GetMapping("/logs")
    suspend fun logs(): String {
        println("访问日志了")
        return tradeQueryLog
    }


    @GetMapping("/sms")
    suspend fun sendSms(): String {
        println("发送测试短信")
        val result = SmsManager.testTradesSms()
        return result
    }


    /**
     * 查询成交记录
     */
    @GetMapping("/trades")
    suspend fun trades(): String {
        val str = OkxHttpRepository.queryTradesHistory()
        return str.toString()
    }


    /**
     * 查询上架记录
     */
    @GetMapping("/listings")
    suspend fun listings(): String {
        val str = OkxHttpRepository.queryOnSalesList()
        return str.toString()
    }

}
