// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used for some aspects of project-wide configuration, like managing plugins, dependencies, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Relocate the local build cache to a CI-controlled directory so it can be persisted across runs.
// gradle/actions/setup-gradle stores the build cache in the Gradle user home and only restores it on
// an *exact* key match, so the populated (~50 MB) cache it saves on main is never restored — every run
// recompiles and retests everything from cold. When GRADLE_BUILD_CACHE_DIR is set (see .github/workflows/ci.yml),
// we point the build cache at a workspace path that ci.yml caches with a prefix-matched actions/cache key,
// giving real cross-commit reuse. Unset locally, so developer builds keep the default Gradle user-home cache.
System.getenv("GRADLE_BUILD_CACHE_DIR")?.let { cacheDir ->
    buildCache {
        local {
            directory = java.io.File(cacheDir)
        }
    }
}

// Cache *retention* (the local build cache had grown to 19 GB) cannot be set from here: Gradle
// rejects it as "modified from an unsafe location" because it governs the shared Gradle user home,
// not this build. It lives in gradle/init.d/argentum-cache-retention.init.gradle.kts instead —
// install it with `just install-gradle-init`.

// Include subprojects in the build.
// If there are changes in only one of the projects, Gradle will rebuild only the one that has changed.
// Learn more about structuring projects with Gradle - https://docs.gradle.org/8.7/userguide/multi_project_builds.html
include(":game-server")
include(":rules-engine")
include(":mtg-sdk")
include(":mtg-sets")
include(":mtg-search")

// The card corpus, split out of :mtg-sets. `:mtg-sets` still re-exports all of it, so every
// existing `project(":mtg-sets")` dependency is unchanged — this only bounds how much Kotlin has to
// compile at once. Era boundaries are FIXED year ranges chained oldest-to-newest: a new release year
// appends a module, and no set ever moves between them.
include(":mtg-sets-core")
include(":mtg-sets-1993-1999")
include(":mtg-sets-2000-2002")
include(":mtg-sets-2003-2007")
include(":mtg-sets-2008-2016")
include(":mtg-sets-2017-2022")
include(":mtg-sets-2023")
include(":mtg-sets-2024")
include(":mtg-sets-2025")
include(":mtg-sets-2026")

// Card scenario tests, mirroring the card modules era for era: a test for an Outlaws of Thunder
// Junction card lives in `:scenario-tests:2024` next to `:mtg-sets-2024`, so a set's PR touches one
// pair of modules. They depend on the `:mtg-sets` aggregator, not on a single era, so every
// scenario still sees the whole catalog — the era only decides where a file lives.
// Engine tests (not about a specific card) stay in `:rules-engine`'s own suite.
include(":scenario-tests:1993-1999")
include(":scenario-tests:2000-2002")
include(":scenario-tests:2003-2007")
include(":scenario-tests:2008-2016")
include(":scenario-tests:2017-2022")
include(":scenario-tests:2023")
include(":scenario-tests:2024")
include(":scenario-tests:2025")
include(":scenario-tests:2026")
include(":ai")
include(":gym")
include(":gym-server")
include(":gym-trainer")
include(":mtgish-tooling")

// Argentum Assay — the first-party Oracle-text parser (docs/oracle-assay.md). Depends on :mtg-sdk
// only: the grammar parses directly into SDK types, and it is not a runtime card loader.
include(":oracle-assay")

rootProject.name = "argentum-engine"
