package com.neusoft.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neusoft.async.EventHandler;
import com.neusoft.async.EventModel;
import com.neusoft.async.EventType;
import com.neusoft.model.Message;
import com.neusoft.model.User;
import com.neusoft.service.MessageService;
import com.neusoft.service.UserService;
import com.neusoft.util.MD5Util;

@Component
public class LikeHandler implements EventHandler {
	@Autowired
	MessageService messageService;
	@Autowired
	UserService userService;

	@Override
	public void doHandle(EventModel model) {
		Message message = new Message();
		message.setFromId(MD5Util.SYSTEM_USERID);
		message.setToId(model.getEntityOwnerId());
		message.setCreatedDate(new Date());
		User user = userService.getUser(model.getActorId());
		message.setContent(
				"用户" + user.getName() + "攒了你的评论，http://127.0.0.1:8080/question/" + model.getExt("questionId"));

		messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventTypes() {

		return Arrays.asList(EventType.LIKE);
	}

}
