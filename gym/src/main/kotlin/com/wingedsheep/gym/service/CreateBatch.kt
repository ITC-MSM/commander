package com.wingedsheep.gym.service

import java.util.concurrent.Callable

/**
 * Create independent game environments in parallel while preserving request order.
 *
 * Singular [MultiEnvService.create] remains the authoritative initialization path. A batch is
 * registry-atomic from the caller's perspective: if any item fails, every environment successfully
 * created by this batch is disposed before the first failure (in request order) is rethrown.
 * This avoids leaking live envs whose IDs could not be returned to the caller.
 */
fun MultiEnvService.createBatch(configs: List<EnvConfig>): List<CreatedEnv> {
    if (configs.isEmpty()) return emptyList()

    val lifecycleLock = Any()
    val createdIds = mutableListOf<EnvId>()
    var abandoned = false
    val tasks = configs.mapIndexed { index, config ->
        Callable<CreateBatchOutcome> {
            try {
                val created = create(config)
                synchronized(lifecycleLock) {
                    // Interrupted callers may have returned while this worker was still initializing.
                    if (abandoned) dispose(listOf(created.envId)) else createdIds.add(created.envId)
                }
                CreateBatchOutcome.Success(created)
            } catch (error: Exception) {
                CreateBatchOutcome.Failure(index, error)
            }
        }
    }
    try {
        val outcomes = workerPool.invokeAll(tasks)
        val failure = outcomes.firstNotNullOfOrNull { it as? CreateBatchOutcome.Failure }
        if (failure != null) throwCreateBatchFailure(failure)
        return outcomes.map { (it as CreateBatchOutcome.Success).created }
    } catch (error: Throwable) {
        synchronized(lifecycleLock) {
            abandoned = true
            dispose(createdIds)
        }
        throw error
    }
}

private sealed interface CreateBatchOutcome {
    data class Success(val created: CreatedEnv) : CreateBatchOutcome
    data class Failure(val index: Int, val error: Exception) : CreateBatchOutcome
}

private fun throwCreateBatchFailure(failure: CreateBatchOutcome.Failure): Nothing {
    val prefix = "create batch item index=${failure.index} failed"
    val error = failure.error
    when (error) {
        is NoSuchElementException ->
            throw NoSuchElementException("$prefix: ${error.message}").also { it.initCause(error) }
        is IllegalArgumentException ->
            throw IllegalArgumentException("$prefix: ${error.message}", error)
        is IllegalStateException ->
            throw IllegalStateException("$prefix: ${error.message}", error)
        else -> throw error
    }
}
