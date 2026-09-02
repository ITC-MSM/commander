package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Nath of the Gilt-Leaf (LRW #250) —
 *   "At the beginning of your upkeep, you may have target opponent discard a card at random."
 *   "Whenever an opponent discards a card, you may create a 1/1 green Elf Warrior creature token."
 *
 * Two things worth proving: the upkeep trigger is a *targeted optional* trigger — the sole
 * opponent is auto-targeted and the "may" is asked on resolution — so declining it must leave the
 * opponent's hand alone; and the discard it causes feeds Nath's own second ability, so accepting
 * both yields exactly one discard and one token.
 */
class NathOfTheGiltLeafScenarioTest : ScenarioTestBase() {

    /**
     * Resolve everything the upkeep put on the stack, answering every yes/no with [acceptDiscard]
     * and any target prompt with the opponent. Returns how many yes/no prompts were asked.
     */
    private fun TestGame.driveUpkeep(acceptDiscard: Boolean): Int {
        var yesNoPrompts = 0
        var guard = 0
        while (guard++ < 20) {
            when (val decision = getPendingDecision()) {
                is ChooseTargetsDecision -> selectTargets(listOf(player2Id))
                is YesNoDecision -> {
                    yesNoPrompts++
                    answerYesNo(acceptDiscard)
                }
                is SelectCardsDecision -> selectCards(decision.options.take(decision.minSelections))
                null -> if (state.stack.isNotEmpty()) resolveStack() else break
                else -> error("unexpected decision $decision")
            }
        }
        return yesNoPrompts
    }

    init {
        context("Nath of the Gilt-Leaf") {

            test("accepting the upkeep trigger discards one card at random and offers an Elf Warrior") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Nath of the Gilt-Leaf")
                    .withCardsInHand(2, "Grizzly Bears", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                // The sole opponent is auto-targeted; the "may" is asked when the trigger resolves,
                // and the discard it causes raises the second trigger's own "may".
                val decisions = game.driveUpkeep(acceptDiscard = true)

                withClue("the opponent discarded exactly one card") {
                    game.handSize(2) shouldBe 2
                    game.graveyardSize(2) shouldBe 1
                }
                withClue("both 'may's were asked — the discard, then the token") {
                    decisions shouldBe 2
                }
                withClue("one 1/1 Elf Warrior token was created") {
                    game.findPermanents("Elf Warrior Token").size shouldBe 1
                }
            }

            test("declining the upkeep trigger leaves the opponent's hand alone and makes no token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Nath of the Gilt-Leaf")
                    .withCardsInHand(2, "Grizzly Bears", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                val decisions = game.driveUpkeep(acceptDiscard = false)

                withClue("only the discard 'may' was asked; nothing was discarded, so no token trigger") {
                    decisions shouldBe 1
                }
                game.handSize(2) shouldBe 3
                game.graveyardSize(2) shouldBe 0
                game.findPermanents("Elf Warrior Token").size shouldBe 0
            }
        }
    }
}
