package com.neusoft.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.dao.LoginTicketDao;
import com.neusoft.dao.UserDao;
import com.neusoft.model.LoginTicket;
import com.neusoft.model.User;
import com.neusoft.util.MD5Util;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private LoginTicketDao loginTicketDao;

	public User getUser(int id) {
		return userDao.selectById(id);
	}

	public Map<String, Object> regist(String username, String password) {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isBlank(username)) {
			map.put("msg", "用户名不能为空");
			return map;
		}
		if (StringUtils.isBlank(password)) {
			map.put("msg", "密码不能为空");
			return map;
		}
		User user = userDao.selectByName(username);
		if (user != null) {
			map.put("msg", "用户名已经被注册");
			return map;
		}
		user = new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0, 5));
		Random random = new Random();
		user.setHeadUrl(String.format("http://localhost:8080/images/img/" + random.nextInt(10) + ".png"));
		user.setPassword(MD5Util.MD5(password + user.getSalt()));
		userDao.addUser(user);

		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		return map;

	}

	public Map<String, Object> login(String username, String password) {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isBlank(username)) {
			map.put("msg", "用户名不能为空");
			return map;
		}
		if (StringUtils.isBlank(password)) {
			map.put("msg", "密码不能为空");
			return map;
		}
		User user = userDao.selectByName(username);
		if (user == null) {
			map.put("msg", "用户名不存在");
			return map;
		}
		if (!MD5Util.MD5(password + user.getSalt()).equals(user.getPassword())) {
			map.put("msg", "密码错误");
			return map;
		}
		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}

	public String addLoginTicket(int userId) {
		LoginTicket loginTicket = new LoginTicket();
		loginTicket.setUserId(userId);
		Date now = new Date();
		now.setTime(3600 * 24 * 100 + now.getTime());
		loginTicket.setExpired(now);
		loginTicket.setStatus(0);
		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
		loginTicketDao.addTicket(loginTicket);
		return loginTicket.getTicket();
	}

	public void logout(String ticket) {
		loginTicketDao.updateStatus(ticket, 1);
	}

	public User selectByName(String name) {
		return userDao.selectByName(name);
	}

	public LoginTicket selectTicketById(int follower) {

		return userDao.selectTicketById(follower);
	}
}
