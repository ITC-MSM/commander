package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cloak and Dagger, Entwined (MSH #211) — {1}{W}{B} Legendary Creature — Human Hero, 2/2,
 * deathtouch + lifelink.
 *
 * "When Cloak and Dagger enter, choose target opponent and up to one target creature they control.
 *  They reveal their hand. You may exile a nonland card from their hand or the chosen creature
 *  until Cloak and Dagger leave the battlefield."
 *
 * The card exists to exercise `CardDestination.ToZoneExiledFrom`: its two exile branches start in
 * *different* zones, and one leaves-the-battlefield trigger has to put each back where it came from
 * (CR 610.3). The discriminating assertions are therefore "hand card goes back to the **hand**, not
 * the battlefield" and "creature goes back to the **battlefield**, not the hand".
 */
class CloakAndDaggerEntwinedScenarioTest : ScenarioTestBase() {

    init {
        context("Cloak and Dagger, Entwined — exile until it leaves, returned to the origin zone") {

            /**
             * Build the board, cast Cloak and Dagger, and answer its ETB targeting with
             * [creatureTarget] (null = decline the "up to one" creature target).
             */
            fun setUp(
                opponentHoldsNonlands: Boolean = true,
                creatureTarget: (TestGame) -> EntityId?,
            ): TestGame {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cloak and Dagger, Entwined")
                    .withCardInHand(1, "Lightning Bolt")
                    // {1}{W}{B} for Cloak and Dagger, {R} for the Bolt that kills it later.
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                if (opponentHoldsNonlands) {
                    // The opponent's hand: two nonland cards (so the pick is a real decision, not
                    // an auto-select) plus a land, which is never an eligible choice.
                    builder = builder
                        .withCardInHand(2, "Serra Angel")
                        .withCardInHand(2, "Shivan Dragon")
                }
                val game = builder
                    .withCardInHand(2, "Forest")
                    // The opponent's creature, the other exile branch.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Cloak and Dagger, Entwined"
                }
                val cast = game.execute(
                    com.wingedsheep.engine.core.CastSpell(game.player1Id, spellId, emptyList())
                )
                withClue("cast should succeed: ${cast.error}") { cast.error shouldBe null }

                // Resolve until the ETB trigger asks for its two targets.
                var guard = 0
                while (game.state.pendingDecision !is ChooseTargetsDecision && guard < 20) {
                    game.resolveStack(); guard++
                }
                val td = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected ChooseTargetsDecision for the ETB; got ${game.state.pendingDecision}")

                val chosenCreature = creatureTarget(game)
                game.submitDecision(
                    TargetsResponse(
                        td.id,
                        mapOf(
                            0 to listOf(game.player2Id),
                            1 to listOfNotNull(chosenCreature),
                        )
                    )
                )
                return game
            }

            /** Resolve until some decision is pending (or the stack empties). */
            fun settle(game: TestGame) {
                var guard = 0
                while (game.state.pendingDecision == null && game.state.stack.isNotEmpty() && guard++ < 20) {
                    game.resolveStack()
                }
            }

            /** Answer the "You may …" yes/no, then pick option [optionIndex] of the ChooseAction. */
            fun choose(game: TestGame, optionIndex: Int) {
                settle(game)
                withClue("the 'You may exile …' gate should prompt before the either/or choice") {
                    (game.state.pendingDecision is ChooseOptionDecision) shouldBe false
                }
                game.answerYesNo(true)
                var guard = 0
                while (game.state.pendingDecision !is ChooseOptionDecision && guard++ < 20) {
                    game.resolveStack()
                }
                val pick = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(pick.id, optionIndex))
            }

            /** Bolt Cloak and Dagger so its leaves-the-battlefield trigger fires. */
            fun killCloakAndDagger(game: TestGame) {
                var guard = 0
                while (game.state.stack.isNotEmpty() && guard++ < 20) {
                    game.resolveStack()
                }
                // The ETB trigger's resolution can leave priority with the non-active player;
                // hand it back before casting at sorcery speed.
                guard = 0
                while (game.state.priorityPlayerId != game.player1Id && guard++ < 10) {
                    game.passPriority()
                }
                val cAndD = game.findPermanent("Cloak and Dagger, Entwined")
                    ?: error("Cloak and Dagger is not on the battlefield")
                val bolt = game.castSpell(1, "Lightning Bolt", cAndD)
                withClue("Lightning Bolt cast should succeed: ${bolt.error}") { bolt.error shouldBe null }
                game.resolveStack()
                withClue("Cloak and Dagger died") {
                    game.isOnBattlefield("Cloak and Dagger, Entwined") shouldBe false
                }
            }

            test("a card exiled from the opponent's hand goes back to their HAND, not the battlefield") {
                val game = setUp { null }
                choose(game, 0) // "Exile a nonland card from their hand"

                val angel = game.findCardsInHand(2, "Serra Angel").firstOrNull()
                    ?: error("Serra Angel should still be in the opponent's hand at the selection")
                game.selectCards(listOf(angel))
                game.resolveStack()

                withClue("the nonland card is exiled by the ETB") {
                    game.isInHand(2, "Serra Angel") shouldBe false
                    game.isInExile(2, "Serra Angel") shouldBe true
                }
                withClue("the land in hand was never a legal choice and stayed put") {
                    game.isInHand(2, "Forest") shouldBe true
                }

                killCloakAndDagger(game)

                withClue("CR 610.3 — it returns to the zone it was exiled from: its owner's hand") {
                    game.isInHand(2, "Serra Angel") shouldBe true
                    game.isInExile(2, "Serra Angel") shouldBe false
                    game.isOnBattlefield("Serra Angel") shouldBe false
                    game.isInGraveyard(2, "Serra Angel") shouldBe false
                }
            }

            test("the chosen creature goes back to the BATTLEFIELD under its owner's control") {
                val game = setUp { it.findPermanent("Grizzly Bears") }
                choose(game, 1) // "Exile the chosen creature"
                game.resolveStack()

                withClue("the chosen creature is exiled by the ETB") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                }
                withClue("the opponent's hand is untouched — only one of the two branches happens") {
                    game.isInHand(2, "Serra Angel") shouldBe true
                }

                killCloakAndDagger(game)

                withClue("CR 610.3 / 610.3c — back onto the battlefield under its owner's control") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInExile(2, "Grizzly Bears") shouldBe false
                    game.isInHand(2, "Grizzly Bears") shouldBe false
                    val bears = game.findPermanent("Grizzly Bears")!!
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
            }

            // The two branches that are dead but still offered. Neither can carry a
            // FeasibilityCheck today (see the card's KDoc): the checks that exist read the
            // *choosing* player's own zones, and these conditions are about the targeted
            // opponent's hand and about whether this ability's own optional target was chosen.
            // Both must therefore resolve as harmless no-ops rather than hanging on a decision.

            test("picking the hand branch with no nonland card in the opponent's hand is a safe no-op") {
                val game = setUp(opponentHoldsNonlands = false) { it.findPermanent("Grizzly Bears") }
                choose(game, 0) // "Exile a nonland card from their hand" — nothing is eligible
                settle(game)

                withClue("an empty eligible set auto-selects nothing instead of pausing") {
                    game.state.pendingDecision shouldBe null
                }
                withClue("nothing left any zone") {
                    game.isInHand(2, "Forest") shouldBe true
                    game.isInExile(2, "Forest") shouldBe false
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }

                killCloakAndDagger(game)

                withClue("the linked-exile pile is empty, so the leaves trigger returns nothing") {
                    game.isInHand(2, "Forest") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("picking the creature branch after declining the optional target is a safe no-op") {
                val game = setUp { null } // no creature target chosen
                choose(game, 1) // "Exile the chosen creature" — there is no chosen creature
                settle(game)

                withClue("an unresolved optional target makes the exile a no-op, not a pause") {
                    game.state.pendingDecision shouldBe null
                }
                withClue("neither zone was touched") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInExile(2, "Grizzly Bears") shouldBe false
                    game.isInHand(2, "Serra Angel") shouldBe true
                    game.isInExile(2, "Serra Angel") shouldBe false
                }

                killCloakAndDagger(game)

                withClue("nothing to return") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInHand(2, "Serra Angel") shouldBe true
                }
            }

            test("declining the may-exile leaves both zones alone, and leaving returns nothing") {
                val game = setUp { it.findPermanent("Grizzly Bears") }
                settle(game)
                game.answerYesNo(false)
                game.resolveStack()

                withClue("nothing was exiled") {
                    game.isInHand(2, "Serra Angel") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInExile(2, "Serra Angel") shouldBe false
                    game.isInExile(2, "Grizzly Bears") shouldBe false
                }

                killCloakAndDagger(game)

                withClue("the leaves trigger is a no-op with an empty linked-exile pile") {
                    game.isInHand(2, "Serra Angel") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
