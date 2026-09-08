package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AdderStaffBoggart
import com.wingedsheep.mtg.sets.definitions.lrw.cards.RebellionOfTheFlamekin
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rebellion of the Flamekin (LRW #188) — "Whenever you clash, you may pay {1}. If you do, create a
 * 3/1 red Elemental Shaman creature token. If you won, that token gains haste until end of turn."
 *
 * Two riders stacked on one trigger, and the test matrix is their cross product:
 *
 *  - The {1} is offered whether or not you won (CR 701.30d is only about the *haste*), and
 *    declining it makes no token at all.
 *  - Haste is a *granted* keyword on the token this resolution created, not an intrinsic one, so
 *    the token has it only on a win — and only until end of turn.
 *
 * The mana payment is also the interesting engine path: `Gate.MayPay` pauses the resolution, so the
 * clash outcome has to survive a suspend/resume to still be readable when the token exists.
 *
 * As with Sylvan Echoes and Entangling Trap, the clash is always started by the *opponent's*
 * Adder-Staff Boggart, matching the card's ruling that you can win a clash you did not initiate.
 */
class RebellionOfTheFlamekinScenarioTest : FunSpec({

    val projector = StateProjector()

    val Boulder = com.wingedsheep.sdk.dsl.card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(RebellionOfTheFlamekin, AdderStaffBoggart, Boulder, Pebble))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.answerClash() {
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, emptyList()))
        }
    }

    /** Player 1's Adder-Staff Boggart clashes; player 2 owns the Rebellion. */
    fun GameTestDriver.opponentClashes() {
        val cardId = putCardInHand(player1, "Adder-Staff Boggart")
        giveMana(player1, Color.RED, 2)
        castSpell(player1, cardId)
        bothPass()
        bothPass()
        answerClash()
    }

    /**
     * Answer the "pay {1}?" offer, auto-tapping for it on a yes, then drain the rest of the
     * resolution. Reports whether the offer was ever made — `Gate.MayPay` checks affordability
     * before prompting, so a player with no mana is never asked.
     */
    fun GameTestDriver.answerPayOne(yes: Boolean): Boolean {
        var asked = false
        var guard = 0
        while (guard++ < 12) {
            when (val decision = pendingDecision) {
                is YesNoDecision -> {
                    asked = true
                    submitYesNo(decision.playerId, yes)
                }
                is SelectManaSourcesDecision -> submitManaAutoPayOrDecline(decision.playerId, true)
                else -> {
                    if (stackSize == 0) return asked
                    bothPass()
                }
            }
        }
        return asked
    }

    fun GameTestDriver.tokens(playerId: EntityId): List<EntityId> =
        getCreatures(playerId).filter { getCardName(it) == "Elemental Shaman Token" }

    test("winning the clash and paying {1} makes a hasty 3/1 Elemental Shaman") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Rebellion of the Flamekin")
        d.giveMana(d.player2, Color.RED, 1)
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")   // clasher: MV 0
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")  // Rebellion's controller: MV 5, wins

        d.opponentClashes()
        d.answerPayOne(yes = true) shouldBe true

        val token = d.tokens(d.player2).single()
        val projected = projector.project(d.state)
        withClue("a 3/1 red Elemental Shaman with haste granted by the win") {
            projected.getPower(token) shouldBe 3
            projected.getToughness(token) shouldBe 1
            projected.hasKeyword(token, Keyword.HASTE) shouldBe true
        }
    }

    test("losing the clash still offers the {1} and still makes the token — just without haste") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Rebellion of the Flamekin")
        d.giveMana(d.player2, Color.RED, 1)
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder")  // clasher wins
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")   // Rebellion's controller loses

        d.opponentClashes()
        withClue("\"you may pay {1}\" sits outside the \"if you won\" rider") {
            d.answerPayOne(yes = true) shouldBe true
        }

        val token = d.tokens(d.player2).single()
        withClue("haste is the only part the clash outcome gates") {
            projector.project(d.state).hasKeyword(token, Keyword.HASTE) shouldBe false
        }
    }

    test("declining the {1} makes no token at all, even on a win") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Rebellion of the Flamekin")
        d.giveMana(d.player2, Color.RED, 1)
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")

        d.opponentClashes()
        d.answerPayOne(yes = false) shouldBe true

        withClue("the token is inside \"if you do\", so a decline skips the whole payoff") {
            d.tokens(d.player2) shouldBe emptyList()
        }
    }

    test("the granted haste is temporary — it is gone the turn after") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Rebellion of the Flamekin")
        d.giveMana(d.player2, Color.RED, 1)
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")

        d.opponentClashes()
        d.answerPayOne(yes = true)
        val token = d.tokens(d.player2).single()
        projector.project(d.state).hasKeyword(token, Keyword.HASTE) shouldBe true

        // A hand over seven cards would stop the walk on a cleanup discard prompt.
        listOf(d.player1, d.player2).forEach { p -> d.getHand(p).forEach { d.moveToGraveyard(it) } }
        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        withClue("baking haste onto the token instead of granting it would leave it here forever") {
            projector.project(d.state).hasKeyword(token, Keyword.HASTE) shouldBe false
        }
    }
})
