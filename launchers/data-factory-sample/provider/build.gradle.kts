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
//    implementation("$groupId:auth-tokenbased:$edcVersion")
    implementation("$groupId:management-api:$edcVersion")
    implementation("$groupId:api-observability:$edcVersion")
    implementation("$groupId:configuration-filesystem:$edcVersion")
    //Catalog
    implementation("$groupId:ids:$edcVersion")
    //IAM
    implementation("$groupId:iam-mock:$edcVersion")
    implementation("$groupId:vault-azure:$edcVersion")
    //Auth
    implementation("$groupId:auth-tokenbased:$edcVersion")
    //Transfer Data
    implementation("$groupId:data-plane-core:$edcVersion")
    implementation("$groupId:azure-resource-manager:$edcVersion")
    implementation(project(":extensions:data-plane:data-plane-azure-data-factory"))

    implementation("$groupId:data-plane-selector-client:$edcVersion")
    implementation("$groupId:data-plane-selector-core:$edcVersion")
    implementation("$groupId:transfer-data-plane:$edcVersion")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("provider.jar")
}