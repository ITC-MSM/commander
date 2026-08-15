package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Stensia Uprising (VOW #178).
 *
 * "{2}{R}{R} Enchantment
 *  At the beginning of your end step, create a 1/1 red Human creature token. Then if you control
 *  exactly thirteen permanents, you may sacrifice this enchantment. When you do, it deals 7 damage
 *  to any target."
 *
 * Exercises the end-step trigger:
 *  - the token is always created;
 *  - when you control exactly thirteen permanents (counting the just-created token), the reflexive
 *    "you may sacrifice → deal 7 damage to any target" is offered, and taking it sacrifices the
 *    enchantment and deals 7 to the chosen target;
 *  - when you don't control exactly thirteen, no sacrifice/damage is offered.
 */
class StensiaUprisingScenarioTest : ScenarioTestBase() {

    init {
        context("Stensia Uprising — end-step trigger") {

            test("creates a 1/1 red Human token; no sacrifice when not at thirteen permanents") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stensia Uprising")
                    .withLandsOnBattlefield(1, "Mountain", 3) // 4 permanents; token → 5 (≠ 13)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("a 1/1 red Human token was created") {
                    game.findPermanent("Human Token").shouldNotBeNull()
                }
                withClue("Stensia Uprising is still on the battlefield — not sacrificed at 5 permanents") {
                    game.findPermanent("Stensia Uprising").shouldNotBeNull()
                }
                withClue("no reflexive sacrifice decision was offered") {
                    game.getPendingDecision().shouldBeNull()
                }
            }

            test("at exactly thirteen permanents, may sacrifice to deal 7 damage to any target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stensia Uprising")
                    .withLandsOnBattlefield(1, "Mountain", 11) // 12 permanents; token → exactly 13
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("token created (12 + 1 = 13 permanents)") {
                    game.findPermanent("Human Token").shouldNotBeNull()
                }

                // Reflexive optional "you may sacrifice this enchantment".
                val decision = game.getPendingDecision()
                decision.shouldNotBeNull()
                decision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true).error shouldBe null

                // "When you do, it deals 7 damage to any target." — target the opponent.
                game.selectTargets(listOf(game.player2Id)).error shouldBe null
                game.resolveStack()

                withClue("Stensia Uprising was sacrificed") {
                    game.findPermanent("Stensia Uprising").shouldBeNull()
                }
                withClue("opponent took 7 damage: 20 → 13") {
                    game.getLifeTotal(2) shouldBe 13
                }
            }

            test("declining the optional sacrifice keeps the enchantment and deals no damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stensia Uprising")
                    .withLandsOnBattlefield(1, "Mountain", 11) // 12 permanents; token → exactly 13
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldNotBeNull()
                decision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("Stensia Uprising stays — sacrifice declined") {
                    game.findPermanent("Stensia Uprising").shouldNotBeNull()
                }
                withClue("no damage dealt") { game.getLifeTotal(2) shouldBe 20 }
            }
        }
    }
}
