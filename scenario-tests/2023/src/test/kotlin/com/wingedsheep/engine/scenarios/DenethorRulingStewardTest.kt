package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.CreaturesDiedThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Denethor, Ruling Steward — "At the beginning of your end step, if a creature died under your
 * control this turn, create a 1/1 white Human Soldier creature token."
 *
 * Regression: Denethor used `Conditions.CreatureDiedThisTurn` (any player's creature) instead of
 * `Conditions.ControlledCreatureDiedThisTurn`, so the intervening-if fired even when only an
 * opponent's creature died — ignoring the "under your control" clause. The death tracker
 * (`CreaturesDiedThisTurnComponent`) is per-player, credited to the dying creature's controller, so
 * scoping the condition to Denethor's controller is the fix.
 */
class DenethorRulingStewardTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
    }

    fun GameTestDriver.soldierTokenCount(playerId: com.wingedsheep.sdk.model.EntityId): Int =
        getCreatures(playerId).count {
            state.getEntity(it)?.get<CardComponent>()?.name == "Human Soldier Token"
        }

    test("creates a Soldier token when a creature died under YOUR control this turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.putPermanentOnBattlefield(you, "Denethor, Ruling Steward")
        // A creature died under your control this turn (engine-maintained per-player tracker).
        d.addComponent(you, CreaturesDiedThisTurnComponent(1))

        d.passPriorityUntil(Step.END)
        while (d.state.stack.isNotEmpty()) d.bothPass()

        d.soldierTokenCount(you) shouldBe 1
    }

    test("does NOT create a token when only an OPPONENT's creature died this turn") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.putPermanentOnBattlefield(you, "Denethor, Ruling Steward")
        // The death happened under the opponent's control — not yours.
        d.addComponent(opponent, CreaturesDiedThisTurnComponent(1))

        d.passPriorityUntil(Step.END)
        while (d.state.stack.isNotEmpty()) d.bothPass()

        d.soldierTokenCount(you) shouldBe 0
    }

    test("does NOT create a token when no creature died this turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.putPermanentOnBattlefield(you, "Denethor, Ruling Steward")

        d.passPriorityUntil(Step.END)
        while (d.state.stack.isNotEmpty()) d.bothPass()

        d.soldierTokenCount(you) shouldBe 0
    }
})
