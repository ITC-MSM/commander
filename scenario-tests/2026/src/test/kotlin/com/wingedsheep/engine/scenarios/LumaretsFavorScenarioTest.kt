package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CopyOfComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Lumaret's Favor {1}{G} Instant — Secrets of Strixhaven #153.
 *
 * "Infusion — When you cast this spell, copy it if you gained life this turn. You may choose new
 * targets for the copy.
 *  Target creature gets +2/+4 until end of turn."
 *
 * The Infusion cast trigger (`WhenYouCastThisSpell` + `Conditions.YouGainedLifeThisTurn`) copies
 * the triggering spell and offers new targets for the copy. The main spell is a +2/+4 pump.
 */
class LumaretsFavorScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        // A simple life-gain spell to turn the Infusion condition on.
        cardRegistry.register(
            CardDefinition.instant(
                name = "Healing Salve Test",
                manaCost = ManaCost.parse("{W}"),
                oracleText = "You gain 3 life.",
                script = CardScript.spell(effect = GainLifeEffect(3, EffectTarget.Controller)),
            )
        )

        context("Lumaret's Favor — Infusion copy") {

            test("no life gained: spell pumps a single creature, no copy") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lumaret's Favor")
                    .withCardOnBattlefield(1, "Grizzly Bears")     // 2/2
                    .withCardOnBattlefield(1, "Hill Giant")        // 3/3
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                game.castSpell(1, "Lumaret's Favor", bears).error shouldBe null
                game.resolveStack()

                withClue("No life gained → no copy, so only the targeted Bears is pumped to 4/6") {
                    projector.getProjectedPower(game.state, bears) shouldBe 4
                    projector.getProjectedToughness(game.state, bears) shouldBe 6
                }
                withClue("Hill Giant is untouched (the copy never happened)") {
                    projector.getProjectedPower(game.state, giant) shouldBe 3
                    projector.getProjectedToughness(game.state, giant) shouldBe 3
                }
            }

            test("life gained: a copy is created and can be retargeted to a second creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lumaret's Favor")
                    .withCardInHand(1, "Healing Salve Test")
                    .withCardOnBattlefield(1, "Grizzly Bears")     // 2/2
                    .withCardOnBattlefield(1, "Hill Giant")        // 3/3
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                // Gain life this turn so Infusion turns on.
                game.castSpell(1, "Healing Salve Test").error shouldBe null
                game.resolveStack()

                // Cast Lumaret's Favor targeting Grizzly Bears.
                game.castSpell(1, "Lumaret's Favor", bears).error shouldBe null

                // The Infusion copy trigger resolves and pauses to let us retarget the copy.
                game.resolveStack()
                withClue("Infusion paused for retargeting the copy") {
                    (game.getPendingDecision() is ChooseTargetsDecision) shouldBe true
                }
                val decision = game.getPendingDecision() as ChooseTargetsDecision
                game.submitDecision(
                    TargetsResponse(decision.id, mapOf(0 to listOf(giant)))
                ).error shouldBe null

                // A copy of Lumaret's Favor is on the stack, targeting the Hill Giant.
                withClue("a spell copy exists on the stack") {
                    val copies = game.state.stack.filter { id ->
                        val c = game.state.getEntity(id)
                        c?.get<SpellOnStackComponent>() != null && c.has<CopyOfComponent>()
                    }
                    copies.size shouldBe 1
                }

                game.resolveStack()

                withClue("original target Grizzly Bears is 4/6") {
                    projector.getProjectedPower(game.state, bears) shouldBe 4
                    projector.getProjectedToughness(game.state, bears) shouldBe 6
                }
                withClue("retargeted copy pumped Hill Giant to 5/7") {
                    projector.getProjectedPower(game.state, giant) shouldBe 5
                    projector.getProjectedToughness(game.state, giant) shouldBe 7
                }
            }
        }
    }
}
