package com.wingedsheep.gameserver.coverage

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

/**
 * Serves the generated readiness gate.  This is deliberately separate from
 * [SetCoverageService]: catalog presence is useful progress information, but it
 * is not proof that a card or the Commander rules are release-ready.
 */
@Service
class CardReadinessService {
    @Serializable
    data class GeneratedFromDTO(
        val commit: String,
        val commitTimestamp: String,
        val catalogSource: String,
        val catalogSha256: String,
        val ledgerSource: String,
        val ledgerSha256: String,
        val triageLedgerSource: String = "coverage/card-implementation-ledger.json",
        val triageLedgerSha256: String? = null,
    )

    @Serializable
    data class SemanticsDTO(
        val catalogCoverage: String,
        val verified: String,
        val releaseEligible: Boolean,
        val releaseEligibleReason: String,
    )

    /** Compact diagnostics. Samples are bounded and never stand in for the ledger. */
    @Serializable
    data class SampleDTO(
        val name: String,
        val sets: List<String> = emptyList(),
        val blocker: String? = null,
    )

    @Serializable
    data class ReadinessDTO(
        val schemaVersion: Int,
        val toolVersion: String,
        val generatedFrom: GeneratedFromDTO,
        val semantics: SemanticsDTO,
        val counts: Map<String, Int>,
        val samples: Map<String, List<SampleDTO>>,
    )

    private val manifest: ReadinessDTO by lazy {
        val raw = ClassPathResource(READINESS_PATH).inputStream.bufferedReader().use { it.readText() }
        Json { ignoreUnknownKeys = true }.decodeFromString<ReadinessDTO>(raw)
    }

    fun readiness(): ReadinessDTO = manifest

    companion object {
        const val READINESS_PATH = "coverage/card-readiness.json"
    }
}
