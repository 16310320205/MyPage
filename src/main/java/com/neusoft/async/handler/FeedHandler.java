package com.neusoft.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.neusoft.async.EventHandler;
import com.neusoft.async.EventModel;
import com.neusoft.async.EventType;
import com.neusoft.model.EntityType;
import com.neusoft.model.Feed;
import com.neusoft.model.Question;
import com.neusoft.model.User;
import com.neusoft.service.FeedService;
import com.neusoft.service.FollowService;
import com.neusoft.service.QuestionService;
import com.neusoft.service.UserService;
import com.neusoft.util.JedisAdapter;
import com.neusoft.util.RedisKeyUtil;

@Component
public class FeedHandler implements EventHandler {
	@Autowired
	FollowService followService;

	@Autowired
	UserService userService;

	@Autowired
	FeedService feedService;

	@Autowired
	JedisAdapter jedisAdapter;

	@Autowired
	QuestionService questionService;

	private String buildFeedData(EventModel model) {
		Map<String, String> map = new HashMap<String, String>();
		// 触发用户是通用的
		User actor = userService.getUser(model.getActorId());
		if (actor == null) {
			return null;
		}
		map.put("userId", String.valueOf(actor.getId()));
		map.put("userHead", actor.getHeadUrl());
		map.put("userName", actor.getName());

		if (model.getType() == EventType.COMMENT
				|| (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
			Question question = questionService.selectById(model.getEntityId());
			if (question == null) {
				return null;
			}
			map.put("questionId", String.valueOf(question.getId()));
			map.put("questionTitle", question.getTitle());
			return JSONObject.toJSONString(map);
		}
		return null;
	}

	@Override
	public void doHandle(EventModel model) {

		// 构造一个新鲜事
		Feed feed = new Feed();
		feed.setCreatedDate(new Date());
		feed.setType(model.getType().getValue());
		feed.setUserId(model.getActorId());
		feed.setData(buildFeedData(model));
		if (feed.getData() == null) {
			// 不支持的feed
			return;
		}
		feedService.addFeed(feed);

		// 获得所有粉丝
		List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(),
				Integer.MAX_VALUE);
		// 系统队列
		followers.add(0);
		// 给所有粉丝推事件
		for (int follower : followers) {
			String timelineKey = RedisKeyUtil.getTimelineKey(follower);
			jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
			// 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(new EventType[] { EventType.COMMENT, EventType.FOLLOW });
	}
}
