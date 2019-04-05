package com.neusoft.async.handler;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neusoft.async.EventHandler;
import com.neusoft.async.EventModel;
import com.neusoft.async.EventType;
import com.neusoft.service.SearchService;

/**
 * Created by neusoft on 2016/8/28.
 */
@Component
public class AddQuestionHandler implements EventHandler {
	private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);
	@Autowired
	SearchService searchService;

	@Override
	public void doHandle(EventModel model) {
		try {
			searchService.indexQuestion(model.getEntityId(), model.getExt("title"), model.getExt("content"));
		} catch (Exception e) {
			logger.error("增加题目索引失败");
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(EventType.ADD_QUESTION);
	}
}