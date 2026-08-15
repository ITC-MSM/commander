// Card definitions for sets released 2000–2002.
//
// Era modules are a chain: each one api-depends on the previous, so a set can reference any set
// released before it. Every cross-set reference in the corpus (basic-land fallbacks, block
// callbacks) points backwards in time, so the chain is acyclic by construction — and a new
// reference that points *forwards* is a compile error rather than a silent tangle.
//
// Boundaries are FIXED. A new release year gets a new module appended to the chain; sets already
// placed never move, so this file's contents only ever grow.
plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    api(project(":mtg-sdk"))
    api(project(":mtg-sets:core"))
    // Chronological chain — everything released earlier.
    api(project(":mtg-sets:1993-1999"))
}
