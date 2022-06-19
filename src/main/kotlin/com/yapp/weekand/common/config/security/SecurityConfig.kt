package com.yapp.weekand.common.config.security

import com.yapp.weekand.common.jwt.JwtAuthenticationFilter
import com.yapp.weekand.common.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter

@Configurable
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
	private val jwtProvider: JwtProvider
) : WebSecurityConfigurerAdapter() {
	@Bean
	fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
	override fun configure(http: HttpSecurity) {
		http
			.httpBasic().disable()
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/graphql","/graphiql").permitAll()
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.addFilterBefore(JwtAuthenticationFilter(jwtProvider), RequestHeaderAuthenticationFilter::class.java)
	}
}
