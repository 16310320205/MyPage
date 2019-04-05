package com.neusoft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.dao.MessageDao;
import com.neusoft.model.Message;

@Service
public class MessageService {
	@Autowired
	private MessageDao messageDao;
	@Autowired
	SensitiveService sensitiveService;

	public int addMessage(Message message) {
		message.setContent(sensitiveService.filter(message.getContent()));
		return messageDao.addMessage(message) > 0 ? message.getId() : 0;
	}

	public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
		return messageDao.getConversationDetail(conversationId, offset, limit);
	}

	public List<Message> getConversationList(int userId, int offset, int limit) {
		return messageDao.getConversationList(userId, offset, limit);
	}

	public int getConversationUnreadCount(int userId, String conversationId) {
		return messageDao.getConvesationUnreadCount(userId, conversationId);

	}
}
