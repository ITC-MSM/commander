// Bound how long entries survive in the shared Gradle user home caches.
//
// Install with `just install-gradle-init` (copies this into ~/.gradle/init.d/).
//
// Why an init script rather than settings.gradle.kts: these caches live in the Gradle user home and
// are shared by every build on the machine, so Gradle refuses to let a project configure them
// ("the property 'entryRetention' was modified from an unsafe location"). It has to be a machine-
// level opt-in, which is also the honest scope — it affects every repo you build, not just this one.
//
// Why it matters here: ~/.gradle/caches/build-cache-1 had reached 19 GB on a developer box. Every
// worktree writes into the same cache, and by default nothing ever expires entries belonging to
// branches that were merged weeks ago. A week is comfortably longer than the useful life of an
// entry — anything that gets rebuilt daily is re-seeded on every build.
beforeSettings {
    caches {
        buildCache { setRemoveUnusedEntriesAfterDays(7) }
        createdResources { setRemoveUnusedEntriesAfterDays(7) }
        downloadedResources { setRemoveUnusedEntriesAfterDays(30) }
        // Daemon logs are small but never pruned; the reaper in `just kill-daemons` reads recent
        // ones, and a fortnight is far more history than that needs.
        daemonLogs { setRemoveUnusedEntriesAfterDays(14) }
    }
}
