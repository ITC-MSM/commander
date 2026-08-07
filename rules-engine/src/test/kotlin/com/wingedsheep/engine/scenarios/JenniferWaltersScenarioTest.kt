package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Jennifer Walters // The Sensational She-Hulk (MSH #18).
 *
 * Front — {1}{W} Legendary Creature — Human Advisor Hero 2/3: "Your opponents can't cast spells
 * during your turn." · "{3}{G}{W}{W}: Transform Jennifer Walters. Activate only as a sorcery."
 *
 * Back — Legendary Creature — Gamma Hero 6/6, reach + trample, the same lock, plus:
 * "Whenever a creature you control is dealt damage, you may have The Sensational She-Hulk deal
 *  that much damage to any target. **Do this only once each turn.**"
 *
 * The back face is why this card needed the per-turn *effect* budget
 * (`TriggeredAbility.effectOncePerTurn`). Per CR 603.2 the ability triggers once per damaged
 * creature, so a multi-block puts one instance on the stack per blocker that was dealt damage and
 * the controller declines down the line to the one they want. Modelling the rider as the *trigger*
 * cap (`oncePerTurn`) would fire only for the first creature dealt damage and make every later
 * instance — including the biggest damage number — unreachable. The primitive itself is covered by
 * [EffectOncePerTurnTest]; this file covers the card.
 */
class JenniferWaltersScenarioTest : ScenarioTestBase() {

    init {
        /**
         * Answer the queued may-questions in order with [answers], choosing [targetId] for each
         * accepted instance. Returns how many questions were asked — i.e. how many instances of the
         * ability triggered.
         */
        fun answerMayQuestions(game: TestGame, answers: List<Boolean>, targetId: EntityId): Int {
            var asked = 0
            var guard = 0
            while (game.hasPendingDecision() && guard++ < 20) {
                val choice = answers.getOrElse(asked) { false }
                game.answerYesNo(choice)
                asked++
                if (choice && game.hasPendingDecision()) {
                    game.selectTargets(listOf(targetId))
                }
                game.resolveStack()
            }
            return asked
        }

        /**
         * Player 2 attacks with three power-2 creatures; player 1 blocks each with a separate
         * creature, so all three blockers are dealt 2 damage in one combat-damage event. Uniform
         * amounts keep the assertion independent of the order the three triggers are offered in.
         */
        fun multiBlockCombat(): TestGame {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "The Sensational She-Hulk")
                .withCardOnBattlefield(1, "Force of Nature")
                .withCardOnBattlefield(1, "Centaur Courser")
                .withCardOnBattlefield(1, "Phantom Warrior")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Goblin Guide")
                .withCardOnBattlefield(2, "Black Creature")
                .withLifeTotal(1, 20)
                .withLifeTotal(2, 20)
                .withActivePlayer(2)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()

            game.declareAttackers(
                mapOf("Grizzly Bears" to 1, "Goblin Guide" to 1, "Black Creature" to 1)
            ).error shouldBe null
            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareBlockers(
                mapOf(
                    "Force of Nature" to listOf("Grizzly Bears"),
                    "Centaur Courser" to listOf("Goblin Guide"),
                    "Phantom Warrior" to listOf("Black Creature"),
                )
            ).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
            return game
        }

        context("The Sensational She-Hulk — the damage mirror") {

            test("every damaged creature offers its own trigger, and only one applies") {
                val game = multiBlockCombat()

                // Accept all three: three blockers were dealt 2 damage each, so three instances
                // must be offered — and the effect budget must still cap the mirror at one.
                val asked = answerMayQuestions(game, listOf(true, true, true), game.player2Id)

                withClue("CR 603.2: one instance per damaged creature") { asked shouldBe 3 }
                withClue("'Do this only once each turn' caps the mirror at a single 2 damage") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("declining the early triggers leaves the later one available") {
                val game = multiBlockCombat()

                val asked = answerMayQuestions(game, listOf(false, false, true), game.player2Id)

                withClue("all three instances were still offered after two declines") {
                    asked shouldBe 3
                }
                withClue("the accepted (third) instance mirrored its 2 damage") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("a declined trigger does not spend the budget — a bigger later hit still mirrors") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Sensational She-Hulk")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(2, "Goblin Guide")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                // Combat: the 2/1 Goblin Guide is blocked by Force of Nature, which is dealt 2.
                game.declareAttackers(mapOf("Goblin Guide" to 1)).error shouldBe null
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(
                    mapOf("Force of Nature" to listOf("Goblin Guide"))
                ).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)

                withClue("the 2-damage trigger is offered and declined") {
                    answerMayQuestions(game, listOf(false), game.player2Id) shouldBe 1
                }
                withClue("nothing was mirrored") { game.getLifeTotal(2) shouldBe 20 }

                // Still the opponent's turn: bolt your own Centaur Courser for a bigger number.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.passPriority() // active player (Player2) passes; Player1 gets priority
                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Lightning Bolt", targetId = courser).error shouldBe null
                game.resolveStack()

                withClue("the declined trigger left the budget intact, so this one is offered") {
                    answerMayQuestions(game, listOf(true), game.player2Id) shouldBe 1
                }
                withClue("She-Hulk mirrors the *later, bigger* 3 damage") {
                    game.getLifeTotal(2) shouldBe 17
                }
            }
        }

        context("Jennifer Walters — the front face lock") {

            test("opponents can't cast spells during your turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jennifer Walters")
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passPriority() // Player1 passes; Player2 gets priority on Player1's turn
                val blocked = game.castSpellTargetingPlayer(2, "Lightning Bolt", 1)

                withClue("PlayersCantCastSpells(EachOpponent, IsYourTurn) refuses the cast") {
                    (blocked.error != null) shouldBe true
                }
                withClue("and Player1 took no damage") { game.getLifeTotal(1) shouldBe 20 }
            }
        }
    }
}
