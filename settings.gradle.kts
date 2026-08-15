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
//
// `removeUnusedEntriesAfterDays` is set unconditionally: Gradle's default retention let the local
// cache grow to 19 GB in ~/.gradle/caches/build-cache-1 on a developer box, because every worktree's
// build writes into the same shared cache and nothing ever expired entries for branches that are long
// merged. A week is comfortably longer than the useful life of an entry here — the sets that actually
// get rebuilt daily are re-seeded on every build.
buildCache {
    local {
        System.getenv("GRADLE_BUILD_CACHE_DIR")?.let { cacheDir ->
            directory = java.io.File(cacheDir)
        }
        removeUnusedEntriesAfterDays = 7
    }
}

// Include subprojects in the build.
// If there are changes in only one of the projects, Gradle will rebuild only the one that has changed.
// Learn more about structuring projects with Gradle - https://docs.gradle.org/8.7/userguide/multi_project_builds.html
include(":game-server")
include(":rules-engine")
include(":mtg-sdk")
include(":mtg-sets")
include(":mtg-search")
include(":ai")
include(":gym")
include(":gym-server")
include(":gym-trainer")
include(":mtgish-tooling")

rootProject.name = "argentum-engine"
