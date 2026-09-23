plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.kotlinPluginSpring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
}

dependencies {
    // HTTP transport for the gym. The gym module is transport-agnostic
    // — all game logic lives there; this module is a thin Spring shell.
    implementation(project(":gym"))
    implementation(project(":rules-engine"))
    implementation(project(":mtg-sdk"))
    implementation(project(":mtg-sets"))

    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterActuator)
    implementation(libs.springdocOpenapi)
    implementation(kotlin("reflect"))

    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.kotestRunner)
    testImplementation(libs.kotestAssertions)
    testImplementation(libs.kotestExtensionsSpring)
}

// Pin the entry point rather than relying on main-class discovery; this one setting feeds
// bootRun, bootJar and resolveMainClassName alike.
springBoot {
    mainClass.set("com.wingedsheep.gym.server.GymServerApplicationKt")
}
