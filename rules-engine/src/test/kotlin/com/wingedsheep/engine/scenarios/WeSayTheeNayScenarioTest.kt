package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * We Say Thee Nay! (MSH #82) — {1}{U} Instant — Arcane.
 *
 *   Teamwork 2
 *   Counter target spell unless its controller pays {2}. Counter that spell unless its controller
 *   pays {4} instead if this spell was cast using teamwork.
 *
 * Both branches are pinned by the same board: Player 2 casts Grizzly Bears with four Forests, two
 * of which pay for it. Two Forests are left untapped — enough for the plain {2} tax, short of the
 * teamwork {4}. So the plain cast produces the "pay?" offer and the teamwork cast counters
 * outright, which is exactly the difference the card prints.
 */
class WeSayTheeNayScenarioTest : ScenarioTestBase() {

    init {
        context("We Say Thee Nay!") {

            fun board() = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "We Say Thee Nay!")
                .withLandsOnBattlefield(1, "Island", 2)
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardInHand(2, "Grizzly Bears")
                .withLandsOnBattlefield(2, "Forest", 4)
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            test("cast without teamwork taxes the spell's controller {2}") {
                val game = board()
                val giant = game.findPermanent("Hill Giant").shouldNotBeNull()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()
                game.castSpellTargetingStackSpell(1, "We Say Thee Nay!", "Grizzly Bears")
                    .error shouldBe null

                game.resolveStack()

                val decision = game.getPendingDecision()
                    .shouldNotBeNull()
                    .shouldBeInstanceOf<YesNoDecision>()
                decision.playerId shouldBe game.player2Id
                withClue("no teamwork was declared, so the tax is the printed {2}") {
                    decision.prompt shouldContain "{2}"
                }
                withClue("nothing was tapped to pay teamwork") {
                    game.state.getEntity(giant)?.has<TappedComponent>() shouldBe false
                }

                // Saying yes opens the mana-source picker; two of the four Forests are still
                // untapped, which is exactly {2}.
                game.answerYesNo(true).error shouldBe null
                game.submitManaSourcesAutoPay().error shouldBe null
                game.resolveStack()

                withClue("Player 2 paid the {2}, so Grizzly Bears is not countered") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("cast using teamwork raises the tax to {4}, which the controller cannot pay") {
                val game = board()
                val giant = game.findPermanent("Hill Giant").shouldNotBeNull()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()

                val bearsOnStack = game.state.stack.first { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                val cardId = game.findCardsInHand(1, "We Say Thee Nay!").first()

                // Teamwork 2 — the 3/3 Hill Giant clears the threshold on its own.
                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(ChosenTarget.Spell(bearsOnStack)),
                        declaredCostSlot = ChoiceSlot.TEAMWORK,
                        additionalCostPayment = AdditionalCostPayment(
                            variableCostPermanents = listOf(giant),
                        ),
                    ),
                ).error shouldBe null
                game.state.getEntity(giant)?.has<TappedComponent>() shouldBe true

                game.resolveStack()

                withClue("two untapped Forests cannot pay {4}, so no offer is even made") {
                    game.getPendingDecision().shouldBeNull()
                }
                withClue("the spell is countered and goes to its owner's graveyard") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
