package com.yapp.weekand.common.config.security

import com.yapp.weekand.common.jwt.JwtAuthenticationFilter
import com.yapp.weekand.common.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configurable
@EnableWebSecurity
class SecurityConfig(
	private val jwtProvider: JwtProvider
) : WebSecurityConfigurerAdapter() {
	@Bean
	fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

	override fun configure(web: WebSecurity) {
		web
			.ignoring().antMatchers("/api/v1/login", "/api/v1/signup", "/api/v1/refresh")
	}

    override fun configure(http: HttpSecurity) {
		http
			.httpBasic().disable()
			.csrf().disable()
			.httpBasic().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/graphql").authenticated()
			.antMatchers("/api/v1/signup", "/api/v1/login", "/api/v1/refresh").permitAll()
			.and()
			.addFilterBefore(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
    }
}
