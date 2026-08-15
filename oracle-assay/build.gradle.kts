plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // The grammar parses straight into `mtg-sdk` types — there is no Assay IR — so the SDK is the
    // ONLY production dependency. Deliberately NOT :rules-engine, NOT :mtg-sets, NOT :mtgish-tooling:
    // Assay must never become a runtime card loader, and it must never inherit the incumbent
    // pipeline's vocabulary. kotlinx-serialization is here to read the Scryfall bulk, not to
    // (de)serialize SDK models.
    implementation(project(":mtg-sdk"))
    implementation(libs.kotlinxSerialization)

    testImplementation(libs.kotestRunner)
    testImplementation(libs.kotestAssertions)
}

application {
    // Single dispatch entrypoint: `parse` / `explain` / `gate` / `report` / `corpus` subcommands.
    // The justfile assay* recipes call these.
    mainClass.set("com.wingedsheep.assay.cli.MainKt")
}

// The corpus cache lives under the user's home, but `assay parse --file` and friends take repo-
// relative paths, so anchor the `run` task at the repo root like :mtgish-tooling does.
tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}
