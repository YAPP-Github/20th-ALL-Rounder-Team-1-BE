package com.yapp.weekand.infra.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService (
	private val redisTemplate: RedisTemplate<String, String>
) {
	fun setValue(key: String, value: String, time: Long) {
		val values = redisTemplate.opsForValue()
		values.set(key, value, Duration.ofMillis(time))
	}

	fun getValue(key: String): String? {
		val values = redisTemplate.opsForValue()
		return values.get(key)
	}

	fun deleteValue(key: String) {
		redisTemplate.delete(key)
	}
}
