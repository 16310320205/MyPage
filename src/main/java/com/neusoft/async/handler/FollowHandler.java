package com.neusoft.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neusoft.async.EventHandler;
import com.neusoft.async.EventModel;
import com.neusoft.async.EventType;
import com.neusoft.model.EntityType;
import com.neusoft.model.Message;
import com.neusoft.model.User;
import com.neusoft.service.MessageService;
import com.neusoft.service.UserService;
import com.neusoft.util.MD5Util;

@Component
public class FollowHandler implements EventHandler {
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

		if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
			message.setContent("用户" + user.getName() + "关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
		} else if (model.getEntityType() == EntityType.ENTITY_USER) {
			message.setContent("用户" + user.getName() + "关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
		}

		messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(EventType.FOLLOW);
	}
}
