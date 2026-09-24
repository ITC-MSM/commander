package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Lich's Relic (FRA #57) — {B} Artifact — Equipment.
 *
 *   When this Equipment enters, you may pay {2}. When you do, for each opponent, destroy up to one
 *   target creature or planeswalker that player controls.
 *   Equipped creature gets +2/+1.
 *   Equip {2}
 *
 * The enters trigger is a reflexive "when you do": paying {2} fires a second ability whose
 * one-per-opponent targets are chosen only then. Declining (or skipping the targets) destroys
 * nothing, and your own creatures are never legal targets.
 */
class LichsRelicScenarioTest : ScenarioTestBase() {

    private fun ScenarioBuilder.relicBoard(): ScenarioBuilder = this
        .withPlayers("Player1", "Player2")
        .withLandsOnBattlefield(1, "Swamp", 5)
        .withCardInHand(1, "Lich's Relic")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withCardOnBattlefield(2, "Hill Giant")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    /**
     * Cast the Relic and walk its enters trigger: answer the "pay {2}?" with [pay], and when the
     * reflexive target prompt appears, record its legal targets and pick [choose].
     */
    private fun TestGame.castRelic(pay: Boolean, choose: (ChooseTargetsDecision) -> List<EntityId>): List<EntityId>? {
        castSpell(1, "Lich's Relic").error shouldBe null
        var offered: List<EntityId>? = null
        var guard = 0
        while (guard++ < 40) {
            when (val decision = state.pendingDecision) {
                is YesNoDecision -> answerYesNo(pay)
                is SelectManaSourcesDecision -> submitManaSourcesAutoPay()
                is ChooseTargetsDecision -> {
                    offered = decision.legalTargets[0] ?: emptyList()
                    selectTargets(choose(decision))
                }
                null -> {
                    if (state.stack.isEmpty()) return offered
                    resolveStack()
                }
                else -> error("unexpected decision: $decision")
            }
        }
        error("decision loop did not settle")
    }

    init {
        context("Lich's Relic") {

            test("paying {2} destroys up to one creature the opponent controls") {
                val game = scenario().relicBoard().build()
                val giant = game.findPermanent("Hill Giant")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val offered = game.castRelic(pay = true) { listOf(giant) }

                withClue("the reflexive trigger offered the opponent's creature but not yours") {
                    offered!! shouldContain giant
                    offered shouldNotContain bears
                }
                withClue("the opponent's creature was destroyed") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInGraveyard(2, "Hill Giant") shouldBe true
                }
                withClue("your own creature is untouched") { game.isOnBattlefield("Grizzly Bears") shouldBe true }
                withClue("the Relic itself resolved onto the battlefield") { game.isOnBattlefield("Lich's Relic") shouldBe true }
            }

            test("declining to pay skips the reflexive trigger entirely") {
                val game = scenario().relicBoard().build()

                val offered = game.castRelic(pay = false) { error("no targets should be asked for") }

                offered shouldBe null
                withClue("nothing was destroyed") { game.isOnBattlefield("Hill Giant") shouldBe true }
            }

            test("up to one — paying and choosing no target destroys nothing") {
                val game = scenario().relicBoard().build()

                game.castRelic(pay = true) { emptyList() }

                withClue("the opponent's creature survives") { game.isOnBattlefield("Hill Giant") shouldBe true }
            }

            test("equipped creature gets +2/+1, and equip costs {2}") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardOnBattlefield(1, "Lich's Relic")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val relic = game.findPermanent("Lich's Relic")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val equipId = cardRegistry.getCard("Lich's Relic")!!.activatedAbilities.first().id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = relic,
                        abilityId = equipId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                ).error shouldBe null
                game.resolveStack()

                game.state.projectedState.getPower(bears) shouldBe 4
                game.state.projectedState.getToughness(bears) shouldBe 3
            }
        }
    }
}
