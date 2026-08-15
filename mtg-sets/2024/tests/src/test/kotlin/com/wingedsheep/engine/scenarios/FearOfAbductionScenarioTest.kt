package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Fear of Abduction (DSK #9) — {4}{W}{W} Enchantment Creature — Nightmare, 5/5, flying.
 *
 * "As an additional cost to cast this spell, exile a creature you control.
 *  When this creature enters, exile target creature an opponent controls.
 *  When this creature leaves the battlefield, put each card exiled with it into its owner's hand."
 *
 * Exercises:
 *  - the `ExileCards(fromZone = BATTLEFIELD)` additional cost (exile a creature you control as the
 *    spell is cast — this exile is NOT linked, so it never returns), and
 *  - the linked ETB exile (`ExileUntilLeaves`) + leaves-trigger `ReturnLinkedExileToHand`, which
 *    returns the exiled opponent's creature to its owner's HAND (not the battlefield).
 */
class FearOfAbductionScenarioTest : ScenarioTestBase() {

    init {
        context("Fear of Abduction — additional-cost exile + linked ETB exile returned to hand") {

            test("cost exiles your creature, ETB exiles an opponent's creature, leaving returns it to hand") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fear of Abduction")
                    .withCardInHand(1, "Doom Blade")
                    // {4}{W}{W} for Fear of Abduction, plus {1}{B} for Doom Blade afterward.
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    // The creature exiled to pay the additional cost.
                    .withCardOnBattlefield(1, "Centaur Courser")
                    // The opponent's creature the ETB will exile.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Fear of Abduction"
                }
                val courser = game.findPermanent("Centaur Courser")!!
                val opponentBears = game.findPermanent("Grizzly Bears")!!

                // Cast, exiling our own Centaur Courser as the additional cost.
                val cast = game.execute(
                    CastSpell(
                        game.player1Id, spellId, emptyList(),
                        additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(courser))
                    )
                )
                withClue("cast should succeed: ${cast.error}") { cast.error shouldBe null }

                withClue("the additional-cost creature is exiled immediately as the spell is cast") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                    game.isInGraveyard(1, "Centaur Courser") shouldBe false
                }

                // Resolve the spell; its ETB trigger pauses to choose the opponent's creature.
                var guard = 0
                while (game.state.pendingDecision !is ChooseTargetsDecision && guard < 20) {
                    game.resolveStack(); guard++
                }
                val td = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected ChooseTargetsDecision for the ETB exile; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(td.id, mapOf(0 to listOf(opponentBears))))
                game.resolveStack()

                withClue("Fear of Abduction resolved onto the battlefield") {
                    game.isOnBattlefield("Fear of Abduction") shouldBe true
                }
                withClue("the opponent's creature is exiled by the ETB") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe false
                    game.isInHand(2, "Grizzly Bears") shouldBe false
                }

                // Destroy Fear of Abduction (Doom Blade — sorcery speed, active player has priority).
                val cast2 = game.castSpell(1, "Doom Blade", game.findPermanent("Fear of Abduction")!!)
                withClue("Doom Blade cast should succeed: ${cast2.error}") { cast2.error shouldBe null }
                game.resolveStack()

                withClue("Fear of Abduction is destroyed") {
                    game.isOnBattlefield("Fear of Abduction") shouldBe false
                    game.isInGraveyard(1, "Fear of Abduction") shouldBe true
                }
                withClue("the linked-exiled creature returns to its OWNER's hand, not the battlefield") {
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("the additional-cost creature stays exiled (it was never linked)") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                    game.isInHand(1, "Centaur Courser") shouldBe false
                    game.isInGraveyard(1, "Centaur Courser") shouldBe false
                }
            }
        }
    }
}
