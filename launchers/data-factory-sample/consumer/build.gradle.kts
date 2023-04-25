plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val groupId: String by project
val edcVersion: String by project

dependencies {
    implementation("$groupId:control-plane-core:$edcVersion")
    implementation("$groupId:http:$edcVersion")
    implementation("$groupId:management-api:$edcVersion")
    implementation("$groupId:api-observability:$edcVersion")
    implementation("$groupId:configuration-filesystem:$edcVersion")

    // API key authentication for Data Management API
    implementation("$groupId:auth-tokenbased:$edcVersion")

    implementation("$groupId:iam-mock:$edcVersion") //For identity service
    implementation("$groupId:vault-azure:$edcVersion")

//    No provider dispatcher registered for protocol: ids-multipart
    implementation("$groupId:ids:$edcVersion") //IDS Multipart Dispatcher API, IDS Transform Extension

    implementation("$groupId:provision-blob:$edcVersion")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("consumer.jar")
}

