package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.LashOut
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Lash Out (LRW #183) — "Lash Out deals 3 damage to target creature. Clash with an opponent. If you
 * win, Lash Out deals 3 damage to that creature's controller."
 *
 * Three things this card can get wrong, one test each:
 *
 *  - **The second 3 goes to the creature's controller, not to the clash opponent.** In a two-player
 *    game those are usually the same player, so the test aims Lash Out at a creature the *caster*
 *    controls: the only board where the two readings disagree, and where the wrong one would burn
 *    the opponent instead of the caster.
 *  - **"That creature's controller" survives the creature dying to the first sentence.** Three
 *    damage kills a 2/1 before the clash even starts, so the payoff has to read last-known
 *    information (CR 608.2h) rather than looking the creature up on the battlefield.
 *  - **The clash still happens when the payoff can't.** The clash is its own sentence; losing it
 *    just means no second 3.
 */
class LashOutScenarioTest : FunSpec({

    val Boulder = com.wingedsheep.sdk.dsl.card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(LashOut, Boulder, Pebble))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.answerClash() {
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, emptyList()))
        }
    }

    fun GameTestDriver.castLashOut(target: EntityId) {
        val cardId = putCardInHand(player1, "Lash Out")
        giveMana(player1, Color.RED, 2)
        castSpellWithTargets(player1, cardId, listOf(ChosenTarget.Permanent(target)))
        bothPass()
    }

    /** Player 1 wins the clash: a {5} on their top against a {0} on the opponent's. */
    fun GameTestDriver.rigWin() {
        putCardOnTopOfLibrary(player1, "Clash Boulder")
        putCardOnTopOfLibrary(player2, "Clash Pebble")
    }

    /** Player 1 loses the clash. */
    fun GameTestDriver.rigLoss() {
        putCardOnTopOfLibrary(player1, "Clash Pebble")
        putCardOnTopOfLibrary(player2, "Clash Boulder")
    }

    test("winning burns the creature's controller — not the clash opponent") {
        val d = driver()
        d.rigWin()
        // Aim at the caster's own creature: the one board where "that creature's controller" and
        // "the opponent you clashed with" are different players.
        val ownCreature = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val myLife = d.getLifeTotal(d.player1)
        val theirLife = d.getLifeTotal(d.player2)

        d.castLashOut(ownCreature)
        d.answerClash()

        withClue("the second 3 follows the creature's controller, so it hits the caster") {
            d.getLifeTotal(d.player1) shouldBe myLife - 3
        }
        withClue("the clash opponent is not the damage recipient") {
            d.getLifeTotal(d.player2) shouldBe theirLife
        }
    }

    test("the creature's controller still takes 3 after the first 3 killed the creature") {
        val d = driver()
        d.rigWin()
        // Grizzly Bears is a 2/2: dead to the first sentence, long before the clash resolves.
        val theirCreature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val theirLife = d.getLifeTotal(d.player2)

        d.castLashOut(theirCreature)
        d.answerClash()

        d.assertInGraveyard(d.player2, "Grizzly Bears")
        withClue("last-known information carries the controller past the creature's death") {
            d.getLifeTotal(d.player2) shouldBe theirLife - 3
        }
    }

    test("losing the clash deals the first 3 and nothing else") {
        val d = driver()
        d.rigLoss()
        val theirCreature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val theirLife = d.getLifeTotal(d.player2)

        d.castLashOut(theirCreature)
        d.answerClash()

        d.assertInGraveyard(d.player2, "Grizzly Bears")
        withClue("no win, no second 3") {
            d.getLifeTotal(d.player2) shouldBe theirLife
        }
    }
})
