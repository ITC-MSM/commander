package com.wingedsheep.gym.server.controller

import com.wingedsheep.gym.server.dto.CreateEnvResponse
import com.wingedsheep.gym.service.EnvConfig
import com.wingedsheep.gym.service.MultiEnvService
import com.wingedsheep.gym.service.createBatch
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Thin HTTP mapping for vectorized game-environment creation. */
@RestController
@RequestMapping("/envs")
@Tag(name = "Environments")
class CreateBatchController(
    private val multiEnvService: MultiEnvService
) {
    @Operation(
        summary = "Create many game envs in parallel",
        description = """
            Parallel counterpart to `POST /envs`. Results preserve request order. If any item fails,
            environments created by the same request are disposed before the first failure is returned,
            so callers never lose track of live env IDs.
        """
    )
    @PostMapping("/create-batch")
    fun createBatch(@RequestBody configs: List<EnvConfig>): List<CreateEnvResponse> =
        multiEnvService.createBatch(configs).map { created ->
            CreateEnvResponse(created.envId, created.observation.observation)
        }
}
