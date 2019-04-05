package com.neusoft.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.neusoft.async.EventModel;
import com.neusoft.async.EventProducer;
import com.neusoft.async.EventType;
import com.neusoft.model.Comment;
import com.neusoft.model.EntityType;
import com.neusoft.model.HostHolder;
import com.neusoft.service.CommentService;
import com.neusoft.service.QuestionService;
import com.neusoft.util.MD5Util;

@Controller
public class CommentController {
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	@Autowired
	HostHolder hostHolder;

	@Autowired
	CommentService commentService;

	@Autowired
	QuestionService questionService;

	@Autowired
	EventProducer eventProducer;

	@RequestMapping(path = { "/addComment" }, method = { RequestMethod.POST })
	public String addComment(@RequestParam("questionId") int questionId, @RequestParam("content") String content) {
		try {
			Comment comment = new Comment();
			comment.setContent(content);
			if (hostHolder.getUser() != null) {
				comment.setUserId(hostHolder.getUser().getId());
			} else {
				comment.setUserId(MD5Util.ANONYMOUS_USERID);
				// return "redirect:/reglogin";
			}
			comment.setCreatedDate(new Date());
			comment.setEntityType(EntityType.ENTITY_QUESTION);
			comment.setEntityId(questionId);
			commentService.addComment(comment);

			int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
			questionService.updateCommentCount(comment.getEntityId(), count);

			eventProducer.fireEvent(
					new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).setEntityId(questionId));

		} catch (Exception e) {
			logger.error("增加评论失败" + e.getMessage());
		}
		return "redirect:/question/" + questionId;
	}
}
