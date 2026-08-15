package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Kutzil's Flanker (LCI #20) — {2}{W} Creature — Cat Warrior, 3/1, Flash, Rare.
 *
 * ETB "choose one": (0) +1/+1 counter for each creature that left the battlefield under your
 * control this turn, (1) gain 2 life and scry 2, (2) exile target player's graveyard.
 *
 * Pins the new `CREATURES_LEFT_BATTLEFIELD` turn tracker (mode 0) and the targeted graveyard-exile
 * mode (mode 2).
 */
class KutzilsFlankerScenarioTest : ScenarioTestBase() {

    init {
        context("Kutzil's Flanker") {

            test("mode 0 counts creatures that left the battlefield under your control this turn") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Grizzly Bears")   // two 2/2s to kill this turn
                    .withCardInHand(1, "Shock")
                    .withCardInHand(1, "Shock")
                    .withCardInHand(1, "Kutzil's Flanker")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                fun castResolving(name: String, targetId: EntityId? = null) {
                    val r = if (targetId != null) game.castSpell(1, name, targetId) else game.castSpell(1, name)
                    withClue("$name should cast: ${r.error}") { r.error shouldBe null }
                    if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                    game.resolveStack()
                }

                // Kill both Grizzly Bears this turn — each leaves the battlefield under your control.
                for (bear in game.findPermanents("Grizzly Bears")) castResolving("Shock", bear)

                // Flash in Kutzil and choose mode 0 (the counter mode).
                castResolving("Kutzil's Flanker")
                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision for the ETB; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 0))
                game.resolveStack()

                val kutzil = game.findPermanent("Kutzil's Flanker")!!
                withClue("two creatures left → two +1/+1 counters on a 3/1 = 5/3") {
                    game.state.projectedState.getPower(kutzil) shouldBe 5
                    game.state.projectedState.getToughness(kutzil) shouldBe 3
                }
            }

            test("mode 2 exiles target player's graveyard") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Kutzil's Flanker")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("precondition: opponent has two cards in graveyard") {
                    game.graveyardSize(2) shouldBe 2
                }

                val cast = game.castSpell(1, "Kutzil's Flanker")
                withClue("Kutzil should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 2))

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the exile mode; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(game.player2Id))))
                game.resolveStack()

                withClue("the targeted player's graveyard is exiled (emptied)") {
                    game.graveyardSize(2) shouldBe 0
                }
            }
        }
    }
}
