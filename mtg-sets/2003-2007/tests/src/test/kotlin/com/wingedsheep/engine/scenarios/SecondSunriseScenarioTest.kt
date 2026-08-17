package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Second Sunrise (MRD #20) — {1}{W}{W} Instant.
 *
 * "Each player returns to the battlefield all artifact, creature, enchantment, and land cards in
 *  their graveyard that were put there from the battlefield this turn."
 *
 * Two clauses carry the card and each has a way of silently doing nothing, so each gets its own
 * assertion:
 *
 *  - *each player … their graveyard* — the opponent's dead come back too, and they come back on the
 *    **opponent's** side. A gather scoped to the caster, or a move that forgot `underOwnersControl`,
 *    would look almost right: the caster's own creature returns either way.
 *  - *put there from the battlefield this turn* — a creature card sitting in a graveyard for any
 *    other reason stays there. Drop the predicate and this reads as a mass reanimation spell.
 */
class SecondSunriseScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 30))
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.settle() {
        var guard = 0
        while (!state.gameOver && state.stack.isNotEmpty() && guard++ < 20) bothPass()
    }

    /** Pyroclasm wipes both 2/2s off the battlefield in one resolution, with no decisions. */
    fun GameTestDriver.wipeBoard(caster: com.wingedsheep.sdk.model.EntityId) {
        val pyroclasm = putCardInHand(caster, "Pyroclasm")
        giveMana(caster, Color.RED, 2)
        castSpell(caster, pyroclasm).isSuccess shouldBe true
        settle()
    }

    fun GameTestDriver.castSecondSunrise(caster: com.wingedsheep.sdk.model.EntityId) {
        val sunrise = putCardInHand(caster, "Second Sunrise")
        giveMana(caster, Color.WHITE, 3)
        castSpell(caster, sunrise).isSuccess shouldBe true
        settle()
    }

    test("both players' creatures return, each under its own owner's control") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        d.putCreatureOnBattlefield(p2, "Grizzly Bears")

        d.wipeBoard(p1)
        d.getGraveyardCardNames(p1) shouldContain "Grizzly Bears"
        d.getGraveyardCardNames(p2) shouldContain "Grizzly Bears"

        d.castSecondSunrise(p1)

        // Back on the battlefield — and the opponent's is on the opponent's side, not the caster's.
        d.findPermanent(p1, "Grizzly Bears").shouldNotBeNull()
        d.findPermanent(p2, "Grizzly Bears").shouldNotBeNull()
        d.getGraveyardCardNames(p1) shouldNotContain "Grizzly Bears"
        d.getGraveyardCardNames(p2) shouldNotContain "Grizzly Bears"
    }

    test("a creature card that reached the graveyard some other way stays there") {
        val d = driver()
        val p1 = d.activePlayer!!

        d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        // Never on the battlefield — a discard or a mill, as far as the graveyard is concerned.
        d.putCardInGraveyard(p1, "Centaur Courser")

        d.wipeBoard(p1)
        d.castSecondSunrise(p1)

        d.findPermanent(p1, "Grizzly Bears").shouldNotBeNull()
        d.getGraveyardCardNames(p1) shouldContain "Centaur Courser"
        d.findPermanent(p1, "Centaur Courser") shouldBe null
    }
})
