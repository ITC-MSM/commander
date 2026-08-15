package io.kotest.provided

import com.wingedsheep.engine.support.HangGuardedProjectConfig

/**
 * Project-wide Kotest config for `:ai` tests — installs the shared anti-hang guard so a runaway
 * test (e.g. an AI search or simulation that loops) fails fast instead of pinning a core for hours.
 * Benchmarks (`-Dbenchmark=true`) opt out of the guard automatically.
 */
class ProjectConfig : HangGuardedProjectConfig()
