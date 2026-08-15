package com.wingedsheep.engine.support

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import kotlin.time.Duration

/**
 * Kotest project config that installs the shared anti-hang guard ([TestHangGuard]).
 *
 * Kotest looks for a config class at the fixed FQN `io.kotest.provided.ProjectConfig`, so it cannot
 * itself live in shared fixtures — every module needs its own class at that name, and each one used
 * to repeat the same two overrides. With the scenario suite split across nine shard modules that
 * would have been eleven identical copies, so the body lives here and each module's config is a
 * one-line subclass:
 *
 * ```
 * package io.kotest.provided
 * class ProjectConfig : HangGuardedProjectConfig()
 * ```
 *
 * Benchmarks (`-Dbenchmark=true`) opt out of the guard automatically — see [TestHangGuard].
 */
abstract class HangGuardedProjectConfig : AbstractProjectConfig() {
    override val timeout: Duration = TestHangGuard.defaultTestTimeout
    override val extensions: List<Extension> = TestHangGuard.extensions()
}
