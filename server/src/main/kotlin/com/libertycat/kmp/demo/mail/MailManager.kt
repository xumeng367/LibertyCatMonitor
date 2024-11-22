package com.libertycat.kmp.demo.mail

import com.libertycat.kmp.demo.beans.Trade
import com.libertycat.kmp.demo.emailsReceivers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.Date

@Component
class MailManager {
    @Autowired
    private lateinit var javaMailSender: JavaMailSender

    fun sendTradeMails(trades: List<Trade>) {
        trades.forEach { trade ->
            sendTradeMail(trade)
        }
    }

    /**
     * 发送成交邮件
     */
    fun sendTradeMail(trade: Trade): Boolean {
        emailsReceivers.forEach { email ->
            try {
                println("发送邮件：$trade ，javaMailSender = $javaMailSender")
                val smm: SimpleMailMessage = SimpleMailMessage()
                // 主题
                smm.setSubject("盯盘喵：有新猫猫#${trade.tokenId}成交了")
                smm.setFrom("2502849497@qq.com")
                // 发送日期
                smm.setSentDate(Date()) //2022-03-01 10:11:47
                // 要发给的邮箱(收件人)
                smm.setTo(email)
                // 抄送邮箱
                smm.setCc("442311638@qq.com")
                // 邮件内容
                smm.setText(
                    "成交价格：$${trade.realPrice()}!订单信息：From:**${trade.from.takeLast(4)}, to:From:**${
                        trade.from.takeLast(
                            4
                        )
                    }"
                )
                javaMailSender?.send(smm)
                println("邮件发送成功:$email")
//                return true
            } catch (e: Exception) {
                e.printStackTrace()
                println("邮件发送成功:$email${e.message}")

            }
//            return false
        }

        return true
    }


}