package com.libertycat.kmp.demo.mail

import com.libertycat.kmp.demo.beans.SalesCat
import com.libertycat.kmp.demo.beans.Trade
import com.libertycat.kmp.demo.emailsReceivers
import com.libertycat.kmp.demo.netwrok.OkxHttpRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File
import java.util.Date

@Component
class MailManager {
    @Autowired
    private lateinit var javaMailSender: JavaMailSender

    suspend fun sendTradeMails(trades: List<Trade>) {
        trades.forEach { trade ->
            sendTradeMail(trade)
        }
    }

    suspend fun sendNewOnSalesCatEmails(salesCats: List<SalesCat>) {
        salesCats.forEach { salesCat ->
            sendNewOnSalesCatEmail(salesCat)
        }
    }


    /**
     * 发送成交邮件
     */
    suspend fun sendTradeMail(trade: Trade) {

        emailsReceivers.forEach { toEmail ->
            try {
                val ntfInfo = OkxHttpRepository.queryNtfInfo(trade.tokenId)
                println("ntfInfo = $ntfInfo")
                sendMails(
                    subject = "盯盘喵：有新喵喵#${trade.tokenId}成交了",
                    text = "成交价格：${trade.realPrice()} ${trade.currency()}!<br>订单信息：<br>From:**${
                        trade.from.takeLast(
                            4
                        )
                    }<br>To  :**${
                        trade.to.takeLast(
                            4
                        )
                    }",
                    tokenId = trade.tokenId,
                    catUrl = ntfInfo.data?.image.toString(),
                    toMail = toEmail
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                println("发送图片失败：${e.message}")
            }
        }
    }


    /**
     * 发送成交邮件
     */
    suspend fun sendNewOnSalesCatEmail(salesCat: SalesCat) {
        emailsReceivers.forEach { toEmail ->
            try {
                sendMails(
                    subject = "盯盘喵：有新喵喵#${salesCat.tokenId}上架了",
                    text = "上架价格：${salesCat.realPrice()}!",
                    tokenId = salesCat.tokenId,
                    catUrl = salesCat.image,
                    toMail = toEmail
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                println("发送图片失败：${e.message}")
            }
        }

    }

    suspend fun sendMails(subject: String, text: String, tokenId: String, catUrl: String, toMail: String) {
        try {
            val downloadImagePath = OkxHttpRepository.downloadImage(catUrl, tokenId)
            println("图片下载地址：$downloadImagePath")
//            val smm: SimpleMailMessage = SimpleMailMessage()
            val message = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            // 主题
            helper.setSubject(subject)
            helper.setFrom("2502849497@qq.com")
            // 发送日期
            helper.setSentDate(Date())
            // 要发给的邮箱(收件人)
            helper.setTo(toMail)
            if (downloadImagePath.isEmpty()) {
                helper.setText(text, true);
            } else {
                helper.setText("$text <img src='cid:image'>", true);
                val file = FileSystemResource(File(downloadImagePath));
                helper.addInline("image", file);
            }

            javaMailSender.send(message)
            println("上架邮件发送成功:$toMail")
//                return true
        } catch (e: Exception) {
            e.printStackTrace()
            println("上架发送成功:$toMail${e.message}")

        }
//            return false
    }

}