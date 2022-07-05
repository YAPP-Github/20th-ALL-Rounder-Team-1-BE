package com.yapp.weekand.infra.s3

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config {
	@Value("\${cloud.aws.credentials.access-key}")
	private lateinit var s3AccessKey: String

	@Value("\${cloud.aws.credentials.secret-key}")
	private lateinit var s3SecretKey: String

	@Value("\${cloud.aws.region.static}")
	private lateinit var regions: Regions

	@Bean
	fun amazonS3(): AmazonS3 {
		return AmazonS3ClientBuilder.standard()
			.withRegion(regions)
			.withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(s3AccessKey, s3SecretKey)))
			.build()
	}
}
