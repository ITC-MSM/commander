package com.wingedsheep.engine.state.components.battlefield

import com.wingedsheep.engine.core.engineSerializersModule
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

/**
 * [AbilityActivatedEverComponent]'s per-object lifetime activation tally, and the
 * [AbilityActivatedEverComponent.abilityIds] fallback that backs it.
 *
 * The tally exists for the permissions that *raise* a keyword's "Activate this ability only once"
 * limit rather than waiving it — [com.wingedsheep.sdk.scripting.ExtraOnceOnlyActivations] with a
 * non-null `extraActivations`, i.e. Wonder Man, Hollywood Hero's "can be activated an additional
 * time" — which have to compare a count against `1 + extra` instead of asking a yes/no question.
 *
 * The fallback is the fragile half and the reason this file exists. Live `GameState` is persisted
 * whole (`RedisGameRepository` / `PersistentGameSession.gameState`), so a game in flight across the
 * deploy that introduced `activationCounts` is restored with that map absent and only `abilityIds`
 * populated. Without the fallback every already-spent once-only ability in that game would re-arm
 * itself — a silent rules break, in exactly the games nobody re-tests. So the legacy-payload case
 * below decodes the real pre-`activationCounts` wire form rather than hand-constructing the
 * component, which is the only version of the test that would catch the field being renamed or the
 * fallback being dropped as dead code.
 */
class AbilityActivatedEverComponentTest : FunSpec({

    val json = Json {
        serializersModule = engineSerializersModule
        ignoreUnknownKeys = true
    }

    val abilityId = AbilityId("ability_1")
    val otherAbilityId = AbilityId("ability_2")

    test("an untracked ability has never been activated") {
        val component = AbilityActivatedEverComponent()

        component.hasActivated(abilityId) shouldBe false
        component.activationCount(abilityId) shouldBe 0
    }

    test("withActivated advances the count and the membership set together") {
        val once = AbilityActivatedEverComponent().withActivated(abilityId)

        once.hasActivated(abilityId) shouldBe true
        once.activationCount(abilityId) shouldBe 1

        val twice = once.withActivated(abilityId)
        withClue("the second activation is what a raise-by-1 permission has to see") {
            twice.activationCount(abilityId) shouldBe 2
        }
        withClue("hasActivated stays a yes/no answer — a plain Once restriction still reads it") {
            twice.hasActivated(abilityId) shouldBe true
        }
        withClue("counts are per ability, not per object") {
            twice.activationCount(otherAbilityId) shouldBe 0
        }
    }

    test("a state serialized before activationCounts existed still reports one activation") {
        // The pre-`activationCounts` wire form, verbatim: only the membership set was written.
        val legacyPayload = """{"abilityIds":["${abilityId.value}"]}"""

        val decoded = json.decodeFromString(
            AbilityActivatedEverComponent.serializer(),
            legacyPayload
        )

        withClue("the map really is absent — otherwise the fallback below proves nothing") {
            decoded.activationCounts shouldBe emptyMap()
        }
        withClue("CR-visible consequence: the spent ability must NOT re-arm across the deploy") {
            decoded.activationCount(abilityId) shouldBe 1
            decoded.hasActivated(abilityId) shouldBe true
        }
        withClue("an ability the legacy state never recorded is still unspent") {
            decoded.activationCount(otherAbilityId) shouldBe 0
        }
    }

    test("the count wins over the fallback once both are present") {
        val decoded = json.decodeFromString(
            AbilityActivatedEverComponent.serializer(),
            """{"abilityIds":["${abilityId.value}"],"activationCounts":{"${abilityId.value}":2}}"""
        )

        decoded.activationCount(abilityId) shouldBe 2
    }
})
