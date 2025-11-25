package com.kt.integration.slack;

// 데브일때는 디스코드로 보내고
// 운영일때는 슬랙으로보내고
public interface NotifyApi {
	void notify(String message);
}
