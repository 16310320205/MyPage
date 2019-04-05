package com.neusoft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.neusoft.dao.QuestionDao;
import com.neusoft.model.Question;

@Service
public class QuestionService {
	@Autowired
	QuestionDao questionDao;
	@Autowired
	SensitiveService sensitiveService;

	public List<Question> getLatestQuestions(int userId, int offset, int limit) {
		return questionDao.selectLatestQuestions(userId, offset, limit);
	}

	public Question selectById(int id) {
		return questionDao.selectById(id);
	}

	public int addQuestion(Question question) {
		// html标签过滤
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		// 敏感词过滤(字典树)
		question.setTitle(sensitiveService.filter(question.getTitle()));
		question.setContent(sensitiveService.filter(question.getContent()));
		return questionDao.addQuestion(question) > 0 ? question.getId() : 0;
	}

	public int updateCommentCount(int entityId, int count) {
		return questionDao.updateCommentCount(entityId, count);
	}
}
