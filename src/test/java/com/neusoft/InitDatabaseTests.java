package com.neusoft;

import java.util.Date;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.neusoft.dao.QuestionDao;
import com.neusoft.dao.UserDao;
import com.neusoft.model.EntityType;
import com.neusoft.model.Question;
import com.neusoft.model.User;
import com.neusoft.service.FollowService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MyPageApplication.class)
@Sql("/init-schema.sql")

public class InitDatabaseTests {
	@Autowired
	UserDao userDao;

	@Autowired
	QuestionDao questionDao;
	@Autowired
	FollowService followService;

	@Test
	public void initDatabase() {
		Random random = new Random();
		for (int i = 0; i < 11; i++) {

			for (int j = 1; j < i; ++j) {
				followService.follow(j, EntityType.ENTITY_USER, i);
			}
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			userDao.addUser(user);
			user.setPassword("xx");
			userDao.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * i);
			question.setCreatedDate(date);
			question.setUserId(i + 1);
			question.setTitle(String.format("TIME{%d}", i));
			question.setContent(String.format("bababababa %d", i));
			questionDao.addQuestion(question);
		}
		Assert.assertEquals("xx", userDao.selectById(1).getPassword());
		userDao.deleteById(1);
		Assert.assertNull(userDao.selectById(1));
		System.out.println(questionDao.selectLatestQuestions(0, 0, 10));
	}
}
