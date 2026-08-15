package io.kotest.provided

import com.wingedsheep.engine.support.HangGuardedProjectConfig

/**
 * Project-wide Kotest config for `:rules-engine` tests — installs the shared anti-hang guard so a
 * runaway test (e.g. a new effect that loops) fails fast instead of pinning a core for hours.
 */
class ProjectConfig : HangGuardedProjectConfig()
