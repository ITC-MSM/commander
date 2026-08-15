// Shared plumbing every set module needs, and nothing else.
//
// `:mtg-sets` was one 12,000-file Kotlin compilation, which is a large part of why the compile
// daemon ran out of heap (docs/build-performance-plan.md). It is now this core, a chain of
// chronological era modules holding the card definitions, and the `:mtg-sets` aggregator that
// re-exports them all. Card definitions import exactly two things from here — CardDiscovery (every
// `MtgSet` uses it to find its own cards) and TokenArtData — so this module has to sit below the
// era chain, and must never grow a dependency on a set.
//
// `definitions/custom/` lives here too: those cards belong to no release (a Vanguard avatar and a
// format helper), so they have no era, and being in core keeps them visible to every set module.
plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Card definitions are public `val`s of SDK types, so the SDK is part of this module's API.
    api(project(":mtg-sdk"))
    implementation(libs.classgraph)
    implementation(libs.kotlinxSerialization)
}
