package com.libertycat.kmp.demo.controller

import com.libertycat.kmp.demo.component.CatMonitorTasks
import com.libertycat.kmp.demo.mail.MailManager
import com.libertycat.kmp.demo.netwrok.OkxHttpRepository
import com.libertycat.kmp.demo.sms.SmsManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CatRestController {

    @Autowired
    private lateinit var mailManager: MailManager

    @Autowired
    private lateinit var catMonitorTasks: CatMonitorTasks
    @GetMapping("/")
    suspend fun home() = "Hello Liberty Cat!"

    @GetMapping("/startTrades")
    suspend fun startTrades(): String {
        catMonitorTasks.startQueryTradesHistoryTask()
        println("Start the trades query service")
        return "Start the trades query service"
    }

    @GetMapping("/startListings")
    suspend fun startListings(): String {
        catMonitorTasks.startQueryOnSalesListTask()
        println("Start the listing query service")
        return "Start the listing query service"
    }


    @GetMapping("/logs")
    suspend fun logs(): String {
        println("print logs")
        return catMonitorTasks.tradeQueryLog
    }

    /**
     * Query trades records
     */
    @GetMapping("/trades")
    suspend fun trades(): String {
        val str = OkxHttpRepository.queryTradesHistory()
        return str.toString()
    }


    /**
     * Query lists records
     */
    @GetMapping("/listings")
    suspend fun listings(): String {
        val str = OkxHttpRepository.queryOnSalesList()
        return str.toString()
    }

    @GetMapping("/test/sms")
    suspend fun sendSms(): String {
        println("test sms")
        val result = SmsManager.testTradesSms()
        return result
    }

    @GetMapping("/test/email")
    suspend fun testMails(): String {
        val result = catMonitorTasks.testSendOnSalesEmails()
        return "send sms emails result: $result"
    }

    @GetMapping("/test/email2")
    suspend fun testMails2(): String {
        val result = catMonitorTasks.testSendTradesEmails()
        return "send trades emails result: $result"
    }

}
