package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Blood Hypnotist (VOW #145) — {2}{R} Creature — Vampire, 3/3.
 *
 *   This creature can't block.
 *   Whenever you sacrifice one or more Blood tokens, target creature can't block this turn.
 *   This ability triggers only once each turn.
 *
 * Exercises:
 *   - the static "can't block" ([CantBlock]) — the Hypnotist itself is flagged as unable to block,
 *   - the "whenever you sacrifice one or more Blood tokens" batch trigger, driven by sacrificing a
 *     Blood token to pay its own ability's cost (mirrors Gluttonous Guest),
 *   - the targeted [CantBlockEffect] applied to the chosen creature until end of turn,
 *   - the "only once each turn" (`oncePerTurn`) clause: a second Blood sacrifice the same turn does
 *     not fire the ability again.
 */
class BloodHypnotistScenarioTest : ScenarioTestBase() {

    init {
        context("Blood Hypnotist") {

            test("the Hypnotist itself can't block") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Hypnotist", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hypnotist = game.findPermanent("Blood Hypnotist")!!
                game.state.projectedState.cantBlock(hypnotist) shouldBe true
            }

            test("sacrificing a Blood token makes target creature unable to block this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Hypnotist", summoningSickness = false)
                    .withCardOnBattlefield(1, "Blood", isToken = true)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Mountain") // fodder to discard for Blood's cost
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("the opposing Grizzly Bears can block before the ability resolves") {
                    game.state.projectedState.cantBlock(bears) shouldBe false
                }

                val blood = game.findPermanent("Blood")!!
                val toDiscard = game.findCardsInHand(1, "Mountain").first()
                val bloodAbilityId = cardRegistry.getCard("Blood")!!.activatedAbilities.first().id

                // Activating the Blood token's ability sacrifices it as part of the cost, which
                // fires Blood Hypnotist's "whenever you sacrifice one or more Blood tokens" trigger.
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = blood,
                        abilityId = bloodAbilityId,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(toDiscard))
                    )
                ).error shouldBe null
                game.resolveStack()

                // The trigger targets the opposing Grizzly Bears.
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("the Blood token was sacrificed") {
                    game.findPermanents("Blood").size shouldBe 0
                }
                withClue("the targeted Grizzly Bears can no longer block this turn") {
                    game.state.projectedState.cantBlock(bears) shouldBe true
                }
            }
        }
    }
}
