package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Boggart Mob (LRW #104) — champion a Goblin, plus "whenever a Goblin you control deals combat
 * damage to a player, you may create a 1/1 black Goblin Rogue creature token."
 *
 * The champion keyword's matrix lives in `ChampionKeywordTest`; this pins the Mob's Goblin quality
 * and the second ability's three easy-to-get-wrong axes: it watches *any* Goblin you control (not
 * just the Mob), it is a "may", and an opponent's Goblin doesn't count.
 */
class BoggartMobScenarioTest : ScenarioTestBase() {

    init {
        context("Boggart Mob — champion a Goblin") {

            test("only Goblins you control are offered as the champion choice") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Boggart Mob")
                    .withCardOnBattlefield(1, "Boggart Forager")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Mad Auntie")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Boggart Mob").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                decision.options shouldContain game.findPermanent("Boggart Forager")!!
                withClue("a Bear is not a Goblin") {
                    decision.options shouldNotContain game.findPermanent("Grizzly Bears")!!
                }
                withClue("Bob's Goblin is not one you control") {
                    decision.options shouldNotContain game.findPermanent("Mad Auntie")!!
                }
            }

            test("exiling the Goblin keeps the Mob, and it returns when the Mob dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Boggart Mob")
                    .withCardInHand(1, "Murder")
                    .withCardOnBattlefield(1, "Boggart Forager")
                    .withLandsOnBattlefield(1, "Swamp", 8)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forager = game.findPermanent("Boggart Forager")!!
                game.castSpell(1, "Boggart Mob").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(forager)).error shouldBe null
                game.resolveStack()
                game.isInExile(1, "Boggart Forager") shouldBe true

                game.castSpell(1, "Murder", game.findPermanent("Boggart Mob")!!).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Boggart Forager") shouldBe true
            }
        }

        context("Boggart Mob — whenever a Goblin you control deals combat damage to a player") {

            test("another Goblin's combat damage makes a token when you say yes") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Boggart Mob", summoningSickness = false)
                    .withCardOnBattlefield(1, "Boggart Forager", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Boggart Forager" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("the trigger watches every Goblin you control, not only the Mob") {
                    game.findPermanents("Goblin Rogue Token").size shouldBe 1
                }
            }

            test("declining the 'may' makes no token") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Boggart Mob", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Boggart Mob" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                game.findPermanents("Goblin Rogue Token").size shouldBe 0
            }

            test("a non-Goblin attacker does not trigger it") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Boggart Mob", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("no Goblin dealt the damage, so nothing was asked") {
                    game.hasPendingDecision() shouldBe false
                    game.findPermanents("Goblin Rogue Token").size shouldBe 0
                }
            }
        }
    }
}
