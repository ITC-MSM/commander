package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Nulldrifter (MH3 #13) — {7} Creature — Eldrazi Elemental 4/4
 *
 *   When you cast this spell, draw two cards.
 *   Flying
 *   Annihilator 1 (Whenever this creature attacks, defending player sacrifices a permanent of their
 *   choice.)
 *   Evoke {2}{U}
 *
 * The first **annihilator** card in the corpus. `KeywordAbility.annihilator` is display-only
 * vocabulary (like rampage / bushido), so the behaviour is lowered on the card as the attack trigger
 * the keyword abbreviates: an edict aimed at `Player.DefendingPlayer` over
 * `GameObjectFilter.Permanent` — annihilator eats *any* permanent, and the defending player picks.
 *
 * The draw is deliberately a **cast** trigger rather than an enters trigger, which is what makes the
 * evoke line work: evoking still casts the spell, so the two cards are drawn even though the body is
 * sacrificed the instant it enters. The third test is the one that proves that ordering.
 */
class NulldrifterScenarioTest : ScenarioTestBase() {

    init {
        context("Nulldrifter") {

            test("casting it draws two cards") {
                var builder = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardInHand(1, "Nulldrifter")
                    .withLandsOnBattlefield(1, "Island", 7)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Island") }
                val game = builder.build()

                withClue("only Nulldrifter is in hand to start") { game.handSize(1) shouldBe 1 }

                game.castSpell(1, "Nulldrifter").error shouldBe null
                game.resolveStack()

                withClue("the cast trigger drew two cards (hand was emptied by the cast itself)") {
                    game.handSize(1) shouldBe 2
                }
                withClue("Nulldrifter resolved onto the battlefield") {
                    game.isOnBattlefield("Nulldrifter") shouldBe true
                }
                withClue("two cards left the library") { game.librarySize(1) shouldBe 3 }
            }

            test("attacking makes the defending player sacrifice a permanent of their choice") {
                val game = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardOnBattlefield(1, "Nulldrifter", summoningSickness = false)
                    // Two permanents, so the defending player actually gets a choice rather than
                    // the executor auto-sacrificing the only legal one.
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Nulldrifter" to 2)).error shouldBe null
                game.resolveStack()

                withClue("annihilator 1 triggered on attack and asks the defender to sacrifice") {
                    game.hasPendingDecision() shouldBe true
                }
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("the decision belongs to the defending player") {
                    decision.playerId shouldBe game.player2Id
                }
                withClue("any permanent is a legal sacrifice — the creature and the land both") {
                    decision.options.size shouldBe 2
                }

                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("the chosen permanent was sacrificed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("the untouched permanent stayed") {
                    game.isOnBattlefield("Forest") shouldBe true
                }
            }

            test("evoked for {2}{U}, it still draws two cards and is then sacrificed on entry") {
                var builder = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardInHand(1, "Nulldrifter")
                    // Exactly the evoke cost. {7} is unpayable here, so a successful cast can only
                    // have been the alternative cost.
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Island") }
                val game = builder.build()

                // Evoke is Nulldrifter's only alternative cost, so the untyped alt-cast is
                // unambiguously the evoke cast.
                game.castSpellWithAlternativeCost(1, "Nulldrifter").error shouldBe null
                game.resolveStack()

                withClue("the cast trigger resolved before the body, so the draw still happened") {
                    game.handSize(1) shouldBe 2
                }
                withClue("evoke sacrificed Nulldrifter as it entered") {
                    game.isOnBattlefield("Nulldrifter") shouldBe false
                    game.isInGraveyard(1, "Nulldrifter") shouldBe true
                }
            }
        }
    }
}
