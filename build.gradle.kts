import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "net.dilius"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val vertxVersion = "4.0.3"
val junitJupiterVersion = "5.7.1"

val mainVerticleName = "net.dilius.some_vertx.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClass.set(launcherClassName)
    @Suppress("DEPRECATION")
    mainClassName = launcherClassName
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web-client")
    implementation("io.vertx:vertx-web-validation")
    implementation("io.vertx:vertx-health-check")
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-zookeeper")
    implementation("io.vertx:vertx-grpc")
    implementation("io.vertx:vertx-micrometer-metrics")
    implementation("io.vertx:vertx-auth-oauth2")
    implementation("io.vertx:vertx-config")
    implementation("io.vertx:vertx-web-graphql")
    implementation("io.vertx:vertx-web-templ-pebble")
    implementation("io.vertx:vertx-pg-client")
    implementation("io.vertx:vertx-web-sstore-cookie")
    implementation("io.vertx:vertx-circuit-breaker")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")
    implementation("io.vertx:vertx-kafka-client")
    implementation("io.vertx:vertx-lang-kotlin")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
