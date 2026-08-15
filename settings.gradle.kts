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

// The card corpus, split out of :mtg-sets and nested underneath it. `:mtg-sets` still re-exports
// all of it, so every existing `project(":mtg-sets")` dependency is unchanged — the split only
// bounds how much Kotlin has to compile at once. Era boundaries are FIXED year ranges chained
// oldest-to-newest: a new release year appends a module, and no set ever moves between them.
//
// Each era also owns its scenario tests as a `tests` child — a test for an Outlaws of Thunder
// Junction card lives in `mtg-sets/2024/tests`, right next to the cards it exercises, so a set's
// PR touches one directory. The test modules depend on the `:mtg-sets` aggregator rather than on
// their own era, so every scenario still sees the whole catalog; the era only decides where a file
// lives. Engine tests (not about a specific card) stay in `:rules-engine`'s own suite.
include(":mtg-sets:core")
for (era in listOf(
    "1993-1999", "2000-2002", "2003-2007", "2008-2016", "2017-2022", "2023", "2024", "2025", "2026",
)) {
    include(":mtg-sets:$era")
    include(":mtg-sets:$era:tests")
}
include(":ai")
include(":gym")
include(":gym-server")
include(":gym-trainer")
include(":mtgish-tooling")

// Argentum Assay — the first-party Oracle-text parser (docs/oracle-assay.md). Depends on :mtg-sdk
// only: the grammar parses directly into SDK types, and it is not a runtime card loader.
include(":oracle-assay")

rootProject.name = "argentum-engine"
