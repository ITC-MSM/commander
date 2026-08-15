package io.kotest.provided

import com.wingedsheep.engine.support.HangGuardedProjectConfig

/**
 * Kotest config for the 2017-2022 scenario shard — installs the shared anti-hang guard so a runaway
 * scenario (e.g. a new card whose trigger loops) fails fast instead of pinning a core for hours.
 */
class ProjectConfig : HangGuardedProjectConfig()
