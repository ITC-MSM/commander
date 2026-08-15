plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.kover)
    `java-test-fixtures`
}

dependencies {
    // SDK dependency - the shared contract
    implementation(project(":mtg-sdk"))

    implementation(libs.bundles.kotlinxEcosystem)

    // Shared test fixtures (GameTestDriver, TestCards) usable by :rules-engine tests
    // and by other modules via testImplementation(testFixtures(project(":rules-engine"))).
    testFixturesImplementation(project(":mtg-sdk"))
    testFixturesImplementation(project(":mtg-sets"))
    testFixturesImplementation(libs.bundles.kotlinxEcosystem)
    testFixturesImplementation(kotlin("reflect"))
    // ScenarioTestBase extends Kotest's FunSpec, so the fixtures need the Kotest API on their classpath.
    testFixturesImplementation(libs.kotestRunner)
    // LegalActionAssertions is a matcher library for consumers, so its assertions are part of the
    // fixtures' API rather than an internal detail.
    testFixturesApi(libs.kotestAssertions)

    testImplementation(project(":mtg-sets"))
    testImplementation(libs.kotestRunner)
    testImplementation(libs.kotestAssertions)
    testImplementation(libs.kotestProperty)
    testImplementation(kotlin("reflect"))
}
