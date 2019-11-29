plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.41"
    application
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val servicetalkVersion by extra { "0.20.0" }

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.servicetalk:servicetalk-http-netty:$servicetalkVersion")
    implementation("io.servicetalk:servicetalk-annotations:$servicetalkVersion")
    implementation("io.servicetalk:servicetalk-http-router-predicate:$servicetalkVersion")
    implementation("io.servicetalk:servicetalk-data-jackson:$servicetalkVersion")

    implementation("org.slf4j:slf4j-api:1.7.28")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

application {
    // Define the main class for the application.
    mainClassName = "com.example.AppKt"
}

tasks.test {
    useJUnitPlatform()
}

