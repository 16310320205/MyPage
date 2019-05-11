package com.neusoft.async;

public enum EventType {
	LIKE(0), COMMENT(1), Regist(2), MAIL(3), FOLLOW(4), UNFOLLOW(5), ADD_QUESTION(6), ADD_MESSAGE(7);

	private int value;

	EventType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
