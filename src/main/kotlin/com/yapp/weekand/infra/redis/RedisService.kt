package com.yapp.weekand.infra.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService (
	private val redisTemplate: RedisTemplate<String, String>
) {
	fun setValue(token: String, email: String, time: Int) {
		val values = redisTemplate.opsForValue()
		values.set(token, email, Duration.ofMinutes(time.toLong()))
	}

	fun getValue(token: String): String? {
		val values = redisTemplate.opsForValue()
		return values.get(token)
	}

	fun deleteValue(token: String) {
		redisTemplate.delete(token)
	}
}
