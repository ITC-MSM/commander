// Container for the per-era scenario test modules — no sources of its own.
//
// It exists to give the whole suite one task path. `:scenario-tests:test` runs every era's tests,
// which is what `just test-rules` and CI use; a single era is still `:scenario-tests:2024:test`.

val scenarioShards = subprojects.map { "${it.path}:test" }

tasks.register("test") {
    group = "verification"
    description = "Run every era's card scenario tests."
    dependsOn(scenarioShards)
}

tasks.register("check") {
    group = "verification"
    description = "Run every era's card scenario tests."
    dependsOn(scenarioShards)
}
