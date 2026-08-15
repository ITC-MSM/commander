package buildsrc.convention

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

/**
 * A build-wide lease that bounds how many Kotlin compilations run concurrently.
 *
 * Why this exists: every Kotlin compile task in the build is executed by the *same* Kotlin compile
 * daemon, so they share one heap. `org.gradle.workers.max` bounds Gradle worker leases, not compiler
 * memory — with six workers, six modules could be in the FIR frontend simultaneously inside a single
 * JVM, and peak heap was the sum of six live compilation sessions rather than the largest one. That
 * is what produced the recurring `OutOfMemoryError: GC overhead limit exceeded` from the compile
 * daemon (see docs/build-performance-plan.md).
 *
 * Registering this as a shared service with `maxParallelUsages = N` makes Gradle hand out at most N
 * leases at a time across the whole build, so peak compile-daemon heap is roughly N × the largest
 * source set instead of workers.max × it. Tune with `-PkotlinCompileParallelism=N`; the default lives
 * in `gradle.properties`.
 *
 * This bounds compilation only — tests, resource processing and the rest of the graph still use the
 * full worker pool, so the throttle costs little wall-clock.
 */
abstract class KotlinCompileThrottle : BuildService<BuildServiceParameters.None>
