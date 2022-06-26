import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.netflix.dgs.codegen") version "5.1.2"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"
    kotlin("plugin.noarg") version "1.6.21"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

noArg {
    annotation("javax.persistence.Entity")

}


group = "com.yapp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    implementation ("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter:latest.release")
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	testImplementation("com.ninja-squad:springmockk:3.1.1")
	implementation("com.coveo:spring-boot-parameter-store-integration:1.1.2")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }

	dependsOn(tasks.generateJava)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
	schemaPaths = mutableListOf("$projectDir/src/main/resources/schema")
	generateClient = false
	packageName = "com.yapp.weekand.api.generated"
	generateDataTypes = true
	language = "kotlin"
	typeMapping = mutableMapOf(
		"Timestamp" to "java.time.LocalDateTime",
		"NotificationType" to "com.yapp.weekand.domain.notification.entity.NotificationType",
		"ScheduleCategoryOpenType" to "com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType",
		"ScheduleStickerName" to "com.yapp.weekand.domain.sticker.entity.ScheduleStickerName",
		"RepeatType" to "com.yapp.weekand.domain.schedule.entity.RepeatType",
		"ScheduleStatus" to "com.yapp.weekand.domain.schedule.entity.Status"
	)
}
