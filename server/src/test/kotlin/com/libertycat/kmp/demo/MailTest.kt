package com.libertycat.kmp.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.junit4.SpringRunner
import java.util.Date
import kotlin.test.Test

/**
 * @创建人: Mhh
 * @创建时间: 2022/3/14
 * @描述: 发送邮件
 */
@SpringBootTest
class MailTest {
    /*
    * 发送邮件对象
    * */
    @Autowired
    private val javaMailSender: JavaMailSender? = null

    //    @Value("${spring.mail.username}")
    //    private String from;
    /*
    * 发送简单的邮件
    * */
    @Test
    fun sendSimpleMail() {
        val smm: SimpleMailMessage = SimpleMailMessage()
        // 主题
        smm.setSubject("发送简单邮件主题")
        // 发件人的邮箱
//        smm.setFrom(from);
        smm.setFrom("2502849497@qq.com")
        // 发送日期
        smm.setSentDate(Date()) //2022-03-01 10:11:47
        // 要发给的邮箱(收件人)
        smm.setTo("1582607598@qq.com")
        // 抄送邮箱
        smm.setCc("442311638@qq.com")
        // 邮件内容
        smm.setText("简单邮件正文\n 您好,今天又是美好的一天")
        // 快速回复
//        smm.setReplyTo("1326303027@qq.com")
        // 密送的邮箱
//        smm.setBcc("1326303027@qq.com")
        // 发送邮件
        javaMailSender?.send(smm)
    }

}

