package com.neusoft.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class MailUtil {

	public static boolean sendEmail(String fromEmail, String toEmail, String[] connent) throws MessagingException {
		Properties prop = new Properties();
		prop.setProperty("mail.host", "stmp.qq.com");
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");
		// 使用javaMail发送邮件的5个步骤
		// 创建session
		Session session = Session.getInstance(prop);
		// 开启session的debug模式，这样就可以查看到成语发送Email的运行状态（测试人员用）session.setDebug(true);
		// 2.通过session得到transport对象
		Transport ts = session.getTransport();
		// 3.使用邮箱的用户名和密码链接上邮件服务器，发送邮件时，发送人需要提交邮箱的用户名和密码（授权码）给smtp服务器，用户名和密码都通过验证之后
		// 才能够正常发送邮件给收件人，QQ邮箱需要使用SSL，端口号465或587      
		// ts.connect("smtp.qq.com",587,"QQ号","授权码");  
		ts.connect("smtp.qq.com", 587, fromEmail, "wdsbtdlulkexhdci");
		// 4、创建邮件  
		Message message;
		try {
			message = createSimpleMail(fromEmail, toEmail, connent, session);
			// 5、发送邮件  
			ts.sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
		}
		ts.close();
		return true;
	}

	/**
	 * 创建邮件
	 * 
	 * @param session
	 * @return
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static Message createSimpleMail(String fromEmail, String toEmail, String[] connent, Session session)
			throws AddressException, MessagingException {
		// 创建邮件
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发送人
		message.setFrom(new InternetAddress(fromEmail));
		// 指明收件人
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		// 设置邮件的标题
		message.setSubject(connent[0]);
		// 设置邮件的内容
		message.setContent(connent[1], "text/html;charset=UTF-8");
		// 返回创建好的邮件对象  
		return message;
	}
}
