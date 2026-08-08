package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Murdock's Crusade (MSH #24) — {1}{W} Sorcery.
 *
 *   Teamwork 4
 *   Choose one. If this spell was cast using teamwork, choose both instead.
 *   • Street Justice — Exile target creature with toughness 4 or greater.
 *   • Legal Justice — Exile target enchantment with mana value 4 or greater.
 *
 * Wall of Swords (3/5) and Castle ({3}{W}, mana value 4) are the two legal victims; Grizzly Bears
 * (2/2) is on the board as the control that the toughness restriction actually restricts.
 */
class MurdocksCrusadeScenarioTest : ScenarioTestBase() {

    init {
        context("Murdock's Crusade") {

            test("cast without teamwork resolves only the one chosen mode") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Murdock's Crusade")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Wall of Swords")
                    .withCardOnBattlefield(2, "Castle")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val wall = game.findPermanent("Wall of Swords").shouldNotBeNull()
                val cardId = game.findCardsInHand(1, "Murdock's Crusade").first()

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(ChosenTarget.Permanent(wall)),
                        chosenModes = listOf(0),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(wall))),
                    ),
                ).error shouldBe null
                game.resolveStack()

                game.isInExile(2, "Wall of Swords") shouldBe true
                withClue("Legal Justice was not chosen, so the enchantment stays") {
                    game.isOnBattlefield("Castle") shouldBe true
                }
                withClue("no teamwork was declared, so nothing tapped") {
                    game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe false
                }
            }

            test("cast using teamwork resolves both modes") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Murdock's Crusade")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Wall of Swords")
                    .withCardOnBattlefield(2, "Castle")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val wall = game.findPermanent("Wall of Swords").shouldNotBeNull()
                val castle = game.findPermanent("Castle").shouldNotBeNull()
                val cardId = game.findCardsInHand(1, "Murdock's Crusade").first()

                // Teamwork 4 — the 6/4 Craw Wurm clears the threshold on its own.
                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Permanent(wall),
                            ChosenTarget.Permanent(castle),
                        ),
                        chosenModes = listOf(0, 1),
                        modeTargetsOrdered = listOf(
                            listOf(ChosenTarget.Permanent(wall)),
                            listOf(ChosenTarget.Permanent(castle)),
                        ),
                        declaredCostSlot = ChoiceSlot.TEAMWORK,
                        additionalCostPayment = AdditionalCostPayment(
                            variableCostPermanents = listOf(wurm),
                        ),
                    ),
                ).error shouldBe null
                game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe true

                game.resolveStack()

                game.isInExile(2, "Wall of Swords") shouldBe true
                game.isInExile(2, "Castle") shouldBe true
            }

            test("choosing both modes without teamwork is rejected") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Murdock's Crusade")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Wall of Swords")
                    .withCardOnBattlefield(2, "Castle")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Wall of Swords").shouldNotBeNull()
                val castle = game.findPermanent("Castle").shouldNotBeNull()
                val cardId = game.findCardsInHand(1, "Murdock's Crusade").first()

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Permanent(wall),
                            ChosenTarget.Permanent(castle),
                        ),
                        chosenModes = listOf(0, 1),
                        modeTargetsOrdered = listOf(
                            listOf(ChosenTarget.Permanent(wall)),
                            listOf(ChosenTarget.Permanent(castle)),
                        ),
                    ),
                ).error.shouldNotBeNull()
                game.isInHand(1, "Murdock's Crusade") shouldBe true
            }
        }
    }
}
