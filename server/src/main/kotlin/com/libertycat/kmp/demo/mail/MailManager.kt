package com.libertycat.kmp.demo.mail

import com.libertycat.kmp.demo.beans.SalesCat
import com.libertycat.kmp.demo.beans.Trade
import com.libertycat.kmp.demo.emailsReceivers
import com.libertycat.kmp.demo.netwrok.OkxHttpRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${mail.username}")
    lateinit var username: String
    suspend fun sendTradeMails(trades: List<Trade>) {
        trades.forEach { trade ->
            if (trade.from.isNotEmpty() && trade.to.isNotEmpty()) {
                sendTradeMail(trade)
            } else {
                println("trades info：$trade")
            }
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
                    subject = "WatchCat: Token #${trade.tokenId} has been sold",
                    text = "Sale price：${trade.realPrice()} ${trade.currency()}!<br>Order info：<br>From:**${
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
                println("Failed to send image：${e.message}")
            }
        }
    }


    /**
     * Send Listing mail
     */
    suspend fun sendNewOnSalesCatEmail(salesCat: SalesCat) {
        emailsReceivers.forEach { toEmail ->
            try {
                sendMails(
                    subject = "WatchCat：Token #${salesCat.tokenId}上架了",
                    text = "Listing price：${salesCat.realPrice()}!",
                    tokenId = salesCat.tokenId,
                    catUrl = salesCat.image,
                    toMail = toEmail
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                println("Failed to send image：${e.message}")
            }
        }

    }

    suspend fun sendMails(subject: String, text: String, tokenId: String, catUrl: String, toMail: String) {
        try {
            val downloadImagePath = OkxHttpRepository.downloadImage(catUrl, tokenId)
            println("Picture url：$downloadImagePath")
//            val smm: SimpleMailMessage = SimpleMailMessage()
            val message = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            helper.setSubject(subject)
            helper.setFrom(username)
            helper.setSentDate(Date())
            helper.setTo(toMail)
            if (downloadImagePath.isEmpty()) {
                helper.setText(text, true);
            } else {
                helper.setText("$text <img src='cid:image'>", true);
                val file = FileSystemResource(File(downloadImagePath));
                helper.addInline("image", file);
            }

            javaMailSender.send(message)
            println("Listing email sent successfully:$toMail")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Listing email sent failed:$toMail${e.message}")

        }
    }

}