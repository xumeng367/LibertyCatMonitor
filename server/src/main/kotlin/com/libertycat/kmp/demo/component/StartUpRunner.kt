package com.libertycat.kmp.demo.component

import com.libertycat.kmp.demo.task.startQueryTradesHistoryTask
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class StartUpRunner {

    @PostConstruct
    fun runAfterStartUp() {
        println("Spring启动后，执行成交查询服务")
        startQueryTradesHistoryTask()
    }
}