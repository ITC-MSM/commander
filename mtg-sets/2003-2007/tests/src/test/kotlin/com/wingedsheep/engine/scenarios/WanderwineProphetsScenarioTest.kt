package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Wanderwine Prophets (LRW #95) — champion a Merfolk, plus "whenever this creature deals combat
 * damage to a player, you may sacrifice a Merfolk. If you do, take an extra turn after this one."
 *
 * The champion keyword's matrix lives in `ChampionKeywordTest`; this pins the Merfolk quality and
 * the extra-turn clause's gate: the turn is owed to the *sacrifice actually happening*, not to
 * saying yes. In a two-player game an extra turn is modelled as the opponent skipping their next,
 * which is what the assertions read.
 */
class WanderwineProphetsScenarioTest : ScenarioTestBase() {

    init {
        context("Wanderwine Prophets — champion a Merfolk") {

            test("only Merfolk you control are offered") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Wanderwine Prophets")
                    .withCardOnBattlefield(1, "Sygg, River Guide")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Wanderwine Prophets").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                decision.options shouldContain game.findPermanent("Sygg, River Guide")!!
                withClue("a Bear is not a Merfolk") {
                    decision.options shouldNotContain game.findPermanent("Grizzly Bears")!!
                }
            }

            test("exiling the Merfolk keeps the Prophets, and it returns when they die") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Wanderwine Prophets")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Sygg, River Guide")
                    .withLandsOnBattlefield(1, "Island", 6)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sygg = game.findPermanent("Sygg, River Guide")!!
                game.castSpell(1, "Wanderwine Prophets").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(sygg)).error shouldBe null
                game.resolveStack()
                game.isInExile(1, "Sygg, River Guide") shouldBe true

                game.castSpell(1, "Doom Blade", game.findPermanent("Wanderwine Prophets")!!)
                    .error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Sygg, River Guide") shouldBe true
            }
        }

        context("Wanderwine Prophets — combat damage, sacrifice a Merfolk, extra turn") {

            test("sacrificing a Merfolk takes an extra turn") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Wanderwine Prophets", summoningSickness = false)
                    .withCardOnBattlefield(1, "Sygg, River Guide")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sygg = game.findPermanent("Sygg, River Guide")!!
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Wanderwine Prophets" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }
                game.answerYesNo(true).error shouldBe null
                if (game.getPendingDecision() is SelectCardsDecision) {
                    game.selectCards(listOf(sygg)).error shouldBe null
                }
                game.resolveStack()

                withClue("the sacrifice happened, so the extra turn is owed") {
                    game.isInGraveyard(1, "Sygg, River Guide") shouldBe true
                    game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe true
                }
            }

            test("declining takes no extra turn") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Wanderwine Prophets", summoningSickness = false)
                    .withCardOnBattlefield(1, "Sygg, River Guide")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Wanderwine Prophets" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("no sacrifice, no extra turn") {
                    game.isOnBattlefield("Sygg, River Guide") shouldBe true
                    game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe false
                }
            }
        }
    }
}
