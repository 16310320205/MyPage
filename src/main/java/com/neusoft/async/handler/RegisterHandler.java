package com.neusoft.async.handler;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neusoft.async.EventHandler;
import com.neusoft.async.EventModel;
import com.neusoft.async.EventType;
import com.neusoft.util.MailUtil;

@Component
public class RegisterHandler implements EventHandler {
	@Autowired
	MailUtil mailUtil;

	@Override
	public void doHandle(EventModel model) {

		// mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆IP异常",
		// "mails/login_exception.html", map);
		String msg[] = new String[3];
		msg[0] = "激活";
		msg[1] = "欢迎:" + model.getExt("username");
		msg[2] = model.getExt("email");
		try {
			mailUtil.sendEmail("1154990071@qq.com", model.getExt("email"), msg);
		} catch (MessagingException e) {

			e.printStackTrace();
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(EventType.Regist);
	}
}
