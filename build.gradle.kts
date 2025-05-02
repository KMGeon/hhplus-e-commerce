import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

val snippetsDir by extra { file("build/generated-snippets")}
val asciidoctorExt: Configuration by configurations.creating
repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Logging
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")

	//validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	//instancio
	testImplementation("org.instancio:instancio-junit:5.4.0")

	//lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// redis
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.redisson:redisson-spring-boot-starter:3.27.0")
	implementation("net.javacrumbs.shedlock:shedlock-spring:5.5.0")
	implementation("net.javacrumbs.shedlock:shedlock-provider-redis-spring:5.5.0")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

	asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// retry
	implementation ("org.springframework.retry:spring-retry")
	implementation ("org.springframework:spring-aspects")


    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:mysql")
	testImplementation("org.testcontainers:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}


tasks {
    bootJar {
        archiveFileName.set("hhplus.jar")
        mainClass.set("kr.hhplus.be.server.ServerApplication")
    }
    jar {
        enabled = false
    }
}