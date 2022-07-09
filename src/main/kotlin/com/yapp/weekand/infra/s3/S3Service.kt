package com.yapp.weekand.infra.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.Headers
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.yapp.weekand.infra.s3.exception.FileUploadFailException
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class S3Service(
	private val amazonS3: AmazonS3
) {
	@Value("\${cloud.aws.s3.bucket}")
	private lateinit var bucketName: String

	fun uploadFile(category: String, multipartFile: MultipartFile): String {
		val fileName: String = buildFileName(category, multipartFile.originalFilename!!)
		val objectMetadata = ObjectMetadata()
		objectMetadata.contentType = multipartFile.contentType
		objectMetadata.contentLength = multipartFile.size
		try {
			amazonS3.putObject(
				PutObjectRequest(bucketName, fileName, multipartFile.inputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead)
			)
		} catch (e: IOException) {
			throw FileUploadFailException()
		}
		return fileName
	}

	fun deleteFile(fileName: String) {
		amazonS3.deleteObject(bucketName, fileName)
	}

	private fun buildFileName(category: String, originalFileName: String): String {
		val fileExtensionIndex: Int = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR)
		val fileExtension = originalFileName.substring(fileExtensionIndex)
		val fileName = originalFileName.substring(0, fileExtensionIndex)
		val now = System.currentTimeMillis().toString()
		return category + CATEGORY_PREFIX + fileName + TIME_SEPARATOR + now + fileExtension
	}

	fun generatePresignedUrl(path: String): String {
		val expiration = DateTime.now().plusMinutes(PRESIGNED_URL_EXPIRE_MINUTE)
		val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, path)
			.withMethod(HttpMethod.PUT)
			.withExpiration(expiration.toDate())

		generatePresignedUrlRequest
			.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString())

		val url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest)
		return url.toString()
	}

	companion object {
		private const val CATEGORY_PREFIX = "/"
		private const val TIME_SEPARATOR = "_"
		private const val FILE_EXTENSION_SEPARATOR = "."
		private const val PRESIGNED_URL_EXPIRE_MINUTE = 5
	}
}
