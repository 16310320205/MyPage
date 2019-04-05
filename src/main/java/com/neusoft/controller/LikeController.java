package com.neusoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.async.EventModel;
import com.neusoft.async.EventProducer;
import com.neusoft.async.EventType;
import com.neusoft.model.Comment;
import com.neusoft.model.EntityType;
import com.neusoft.model.HostHolder;
import com.neusoft.service.CommentService;
import com.neusoft.service.LikeService;
import com.neusoft.util.MD5Util;

@Controller
public class LikeController {
	@Autowired
	LikeService likeService;
	@Autowired
	HostHolder hostHolder;
	@Autowired
	EventProducer eventProducer;
	@Autowired
	CommentService commentService;

	@RequestMapping(path = { "/like" }, method = { RequestMethod.POST })
	@ResponseBody
	private String like(@RequestParam("commentId") int commentId) {
		if (hostHolder == null) {
			return MD5Util.getJSONString(999);
		}
		Comment comment = commentService.getCommentById(commentId);
		eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
				.setEntityId(commentId).setEntityType(EntityType.ENTITY_COMMENT)
				.setExt("questionId", String.valueOf(comment.getEntityId())).setEntityOwnerId(comment.getUserId()));
		long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return MD5Util.getJSONString(0, String.valueOf(likeCount));
	}

	@RequestMapping(path = { "/dislike" }, method = { RequestMethod.POST })
	@ResponseBody
	private String dislike(@RequestParam("commentId") int commentId) {
		if (hostHolder == null) {
			return MD5Util.getJSONString(999);
		}
		long disLikeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return MD5Util.getJSONString(0, String.valueOf(disLikeCount));
	}
}
