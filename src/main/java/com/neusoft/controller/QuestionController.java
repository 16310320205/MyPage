package com.neusoft.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.neusoft.model.Question;
import com.neusoft.model.User;
import com.neusoft.model.ViewObject;
import com.neusoft.service.CommentService;
import com.neusoft.service.FollowService;
import com.neusoft.service.LikeService;
import com.neusoft.service.QuestionService;
import com.neusoft.service.UserService;
import com.neusoft.util.MD5Util;

/**
 * Created by neusoft on 2016/7/22.
 */
@Controller
public class QuestionController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	QuestionService questionService;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	UserService userService;

	@Autowired
	CommentService commentService;

	@Autowired
	FollowService followService;

	@Autowired
	LikeService likeService;

	@Autowired
	EventProducer eventProducer;

	@RequestMapping(value = "/question/{qid}", method = { RequestMethod.GET })
	public String questionDetail(Model model, @PathVariable("qid") int qid) {
		Question question = questionService.selectById(qid);
		model.addAttribute("question", question);

		List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
		List<ViewObject> comments = new ArrayList<ViewObject>();
		for (Comment comment : commentList) {
			ViewObject vo = new ViewObject();
			vo.set("comment", comment);
			if (hostHolder.getUser() == null) {
				vo.set("liked", 0);
			} else {
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,
						comment.getId()));
			}

			vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			vo.set("user", userService.getUser(comment.getUserId()));
			comments.add(vo);
		}

		model.addAttribute("comments", comments);

		List<ViewObject> followUsers = new ArrayList<ViewObject>();
		// 获取关注的用户信息
		List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
		for (Integer userId : users) {
			ViewObject vo = new ViewObject();
			User u = userService.getUser(userId);
			if (u == null) {
				continue;
			}
			vo.set("name", u.getName());
			vo.set("headUrl", u.getHeadUrl());
			vo.set("id", u.getId());
			followUsers.add(vo);
		}
		model.addAttribute("followUsers", followUsers);
		if (hostHolder.getUser() != null) {
			model.addAttribute("followed",
					followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
		} else {
			model.addAttribute("followed", false);
		}

		return "detail";
	}

	@RequestMapping(value = "/question/add", method = { RequestMethod.POST })
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content) {
		try {
			Question question = new Question();
			question.setContent(content);
			question.setCreatedDate(new Date());
			question.setTitle(title);
			if (hostHolder.getUser() == null) {
				question.setUserId(MD5Util.ANONYMOUS_USERID);
				// return MD5Util.getJSONString(999);
			} else {
				question.setUserId(hostHolder.getUser().getId());
			}
			if (questionService.addQuestion(question) > 0) {
				eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION).setActorId(question.getUserId())
						.setEntityId(question.getId()).setExt("title", question.getTitle())
						.setExt("content", question.getContent()));
				return MD5Util.getJSONString(0);
			}
		} catch (Exception e) {
			logger.error("增加题目失败" + e.getMessage());
		}
		return MD5Util.getJSONString(1, "失败");
	}

}
