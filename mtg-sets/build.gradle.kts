// The card corpus, as seen by everyone else.
//
// This module used to hold all 12,000 card-definition files in one Kotlin compilation — the single
// biggest compile unit in the build and a main cause of the compile daemon running out of heap
// (docs/build-performance-plan.md). The definitions now live in `:mtg-sets:core` plus a chain of
// chronological `:mtg-sets:<era>` modules, and this module re-exports all of them with `api`, so
// every existing `project(":mtg-sets")` dependency keeps resolving the whole corpus unchanged.
//
// What stays here is the code that needs to see *every* set at once: MtgSetCatalog (a classpath
// scan, so it has no compile dependency on any era), the Scryfall sync tools, the predefined-token
// registry, and the whole-corpus tests (snapshots, lint, facade boundary).
//
// Each era additionally owns its card scenario tests as a `tests` child (`:mtg-sets:2024:tests`),
// so a set's cards and the tests that exercise them live in one directory. `scenarioTest` below is
// the one task path that runs all of them.
plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(project(":mtg-sdk"))
    implementation(libs.classgraph)
    implementation(libs.kotlinxSerialization)

    // Re-export the whole corpus. `api`, not `implementation`: consumers import card definitions
    // directly (scenario tests, TestCards), so the era modules have to reach their compile classpath.
    api(project(":mtg-sets:core"))
    api(project(":mtg-sets:1993-1999"))
    api(project(":mtg-sets:2000-2002"))
    api(project(":mtg-sets:2003-2007"))
    api(project(":mtg-sets:2008-2016"))
    api(project(":mtg-sets:2017-2022"))
    api(project(":mtg-sets:2023"))
    api(project(":mtg-sets:2024"))
    api(project(":mtg-sets:2025"))
    api(project(":mtg-sets:2026"))

    testImplementation(libs.kotestRunner)
    testImplementation(libs.kotestAssertions)
}

// One task path for the whole card scenario suite. The tests are spread over a `tests` module per
// era, so no single project owns them; this fans out to every one, which is what `just test-rules`
// and CI use. A single era is still `:mtg-sets:2024:tests:test`.
//
// NOTE this is deliberately *not* wired into `check`: `:mtg-sets:check` is the whole-corpus gate
// (snapshots, lint, facade boundary) and must stay runnable without the multi-minute card suite.
val scenarioShards = subprojects.filter { it.name == "tests" }.map { "${it.path}:test" }

tasks.register("scenarioTest") {
    group = "verification"
    description = "Run every era's card scenario tests."
    dependsOn(scenarioShards)
}

tasks.withType<Test> {
    systemProperty("verifyImageUris", System.getProperty("verifyImageUris") ?: "false")
    // Forward the snapshot re-bless switch into the forked test JVM (see CardDefinitionSnapshotTest).
    System.getProperty("updateSnapshots")?.let { systemProperty("updateSnapshots", it) }
}

// One-shot Scryfall sync — populates legalities.json from the live Scryfall API.
// Run with: ./gradlew :mtg-sets:syncLegality
tasks.register<JavaExec>("syncLegality") {
    description = "Fetch deck-format legality for every registered card from Scryfall."
    group = "build"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.wingedsheep.mtg.sets.legality.SyncLegalitiesKt")
    workingDir = rootProject.projectDir
}

// One-shot Scryfall sync — populates tokens.json with every token printing of every registered
// set, read at runtime by TokenArtData so a created token shows its own set's art.
// Run with: ./gradlew :mtg-sets:syncTokenArt
tasks.register<JavaExec>("syncTokenArt") {
    description = "Fetch per-set token art from Scryfall's token sets into tokens.json."
    group = "build"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.wingedsheep.mtg.sets.tokens.SyncTokenArtKt")
    workingDir = rootProject.projectDir
}

// Report every token our cards create that has no set-scoped art — the work list for self-hosting
// art for sets Scryfall has no token printings for.
// Run with: just token-art-gaps   (or ./gradlew :mtg-sets:tokenArtGaps)
tasks.register<JavaExec>("tokenArtGaps") {
    description = "List tokens with no set-scoped art into backlog/token-art-gaps.md."
    group = "verification"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.wingedsheep.mtg.sets.tokens.TokenArtGapsKt")
    workingDir = rootProject.projectDir
}

// Offline sync from a Scryfall bulk-data dump. Pass the dump path via --args.
// Run with: ./gradlew :mtg-sets:syncLegalityFromDump --args="/path/to/all-cards.json"
tasks.register<JavaExec>("syncLegalityFromDump") {
    description = "Populate legalities.json from a local Scryfall bulk-data dump."
    group = "build"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.wingedsheep.mtg.sets.legality.SyncLegalitiesFromDumpKt")
    workingDir = rootProject.projectDir
}

// Offline sync of color identities. Walks every card definition .kt file under
// mtg-sets/.../definitions/<set>/cards/ and adds or updates `colorIdentity = "..."` from a
// Scryfall bulk-data dump.
// Run with: ./gradlew :mtg-sets:syncColorIdentityFromDump --args="/path/to/all-cards.json"
tasks.register<JavaExec>("syncColorIdentityFromDump") {
    description = "Patch every card .kt file with its Scryfall color identity."
    group = "build"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.wingedsheep.mtg.sets.colors.SyncColorIdentityFromDumpKt")
    workingDir = rootProject.projectDir
}

// === mtgish auto-generator: compile-verification gate (Hybrid design) ===========================
// The Kotlin mtgish emitter writes draft cards into this isolated source set under a
// distinct `generated.<set>.cards` package (never colliding with the real definitions on the
// classpath). Gradle compiles them — so a draft that doesn't compile fails the build — and the
// verifier serialises each via the same CardExporter that produces the golden snapshots. A small
// `fidelity --gate` step then gameplay-tree diffs the serialised trees against the golden, turning the
// fidelity AUTO tier from a static prediction into a real "compiles + golden-equivalent" check.
// Run with: just coverage-verify --set POR   (or ./gradlew :mtg-sets:verifyGeneratedCards -Pset=POR)
val generatedCardsDir = layout.buildDirectory.dir("generated-cards/src")
val generatorSet = (project.findProperty("set") as String? ?: "POR").toString().uppercase()

sourceSets { create("generatedCards") }
kotlin.sourceSets.named("generatedCards") { kotlin.srcDir(generatedCardsDir) }
dependencies { "generatedCardsImplementation"(project(":mtg-sdk")) }

// Run the :mtgish-tooling CLI's emit-all on its runtime classpath (build-time tool dependency only — keeps
// :mtg-sets's main classpath free of the mtgish tooling).
val mtgishTool: Configuration =
    configurations.create("mtgishTool") { isCanBeResolved = true; isCanBeConsumed = false }
dependencies { mtgishTool(project(":mtgish-tooling")) }

val emitGeneratedCards = tasks.register<JavaExec>("emitGeneratedCards") {
    description = "Emit whole-renderable cards for -Pset=CODE via the mtgish bridge."
    group = "verification"
    workingDir = rootProject.projectDir
    classpath = mtgishTool
    mainClass.set("com.wingedsheep.tooling.coverage.MainKt")
    args("autogen", "--set", generatorSet, "--emit-all", "--out", generatedCardsDir.get().asFile.absolutePath)
}
tasks.named("compileGeneratedCardsKotlin") { dependsOn(emitGeneratedCards) }

tasks.register<JavaExec>("verifyGeneratedCards") {
    description = "Compile the mtgish-generated cards and serialise them for the capability gate."
    group = "verification"
    dependsOn("compileGeneratedCardsKotlin")
    classpath = files(sourceSets["generatedCards"].output, sourceSets["main"].runtimeClasspath)
    mainClass.set("com.wingedsheep.mtg.sets.codegen.GeneratedCardVerifierKt")
    workingDir = rootProject.projectDir
    args(
        "com.wingedsheep.mtg.sets.generated.${generatorSet.lowercase()}.cards",
        layout.buildDirectory.file("generated-cards/${generatorSet.lowercase()}.generated.json")
            .get().asFile.absolutePath
    )
}
