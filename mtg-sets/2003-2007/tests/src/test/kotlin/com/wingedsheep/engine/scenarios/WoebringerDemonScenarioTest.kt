package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Woebringer Demon (RAV #113) — {3}{B}{B} Creature — Demon 4/4, flying.
 *
 * "At the beginning of each player's upkeep, that player sacrifices a creature of their choice.
 * If the player can't, sacrifice this creature."
 *
 * The whole card is the "can't" clause, and it is the half a board scan gets wrong: by the time
 * a later step could count creatures, the edict has already taken one. The script instead reads
 * `DynamicAmount.PermanentsSacrificedThisWay` — what this resolution actually took — so the
 * fallback fires exactly when the edict came up empty. Both directions are pinned below: an
 * opponent with an empty board kills the Demon, and an opponent with one creature does not.
 */
class WoebringerDemonScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        return d
    }

    fun GameTestDriver.advanceToUpkeepOf(player: EntityId) {
        passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (activePlayer != player) {
            passPriorityUntil(Step.DRAW, maxPasses = 200)
            passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        currentStep shouldBe Step.UPKEEP
        activePlayer shouldBe player
    }

    test("an opponent who controls no creature can't sacrifice, so the Demon is sacrificed") {
        val d = driver()
        val controller = d.player1
        val opponent = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val demon = d.putCreatureOnBattlefield(controller, "Woebringer Demon")

        d.advanceToUpkeepOf(opponent)
        d.stackSize shouldBe 1
        d.bothPass()

        withClue("nothing was sacrificed this way, so the fallback takes the Demon") {
            d.state.getBattlefield().contains(demon) shouldBe false
            d.getGraveyard(controller).contains(demon) shouldBe true
        }
    }

    test("an opponent who controls a creature sacrifices it and the Demon survives") {
        val d = driver()
        val controller = d.player1
        val opponent = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val demon = d.putCreatureOnBattlefield(controller, "Woebringer Demon")
        val bear = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        d.advanceToUpkeepOf(opponent)
        d.stackSize shouldBe 1
        d.bothPass()

        withClue("one legal sacrifice needs no prompt — the bear goes, the Demon stays") {
            d.state.getBattlefield().contains(bear) shouldBe false
            d.state.getBattlefield().contains(demon) shouldBe true
        }
    }

    test("on its controller's upkeep the choice is theirs, and it need not be the Demon") {
        val d = driver()
        val controller = d.player1
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val demon = d.putCreatureOnBattlefield(controller, "Woebringer Demon")
        val bear = d.putCreatureOnBattlefield(controller, "Grizzly Bears")
        // The opponent's upkeep comes first and must not eat the Demon on the way past, so give
        // them a creature of their own to feed the edict.
        d.putCreatureOnBattlefield(d.player2, "Centaur Courser")

        d.advanceToUpkeepOf(controller)
        d.stackSize shouldBe 1
        d.bothPass()

        val decision = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe controller
        d.submitCardSelection(controller, listOf(bear))

        withClue("the Demon is itself a legal sacrifice, so its controller is never forced to lose it") {
            d.state.getBattlefield().contains(bear) shouldBe false
            d.state.getBattlefield().contains(demon) shouldBe true
        }
    }
})
