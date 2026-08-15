package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.AtalyaSamiteMaster
import com.wingedsheep.mtg.sets.definitions.inv.cards.SoulBurn
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Invasion engine gap #8 — color-restricted `{X}` spend + per-color mana-spent-on-X tracking.
 *
 *  - **Soul Burn** ({X}{2}{B}, "spend only black and/or red mana on X"): exercises both the
 *    `xManaRestriction` (X payable only with black/red) and `DynamicAmount.ManaSpentOnX`
 *    (life gained = black mana spent on X).
 *  - **Atalya, Samite Master** ({X},{T}, "spend only white mana on X"): exercises the same
 *    restriction on the activated-ability payment path.
 */
class SoulBurnAndAtalyaXManaTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SoulBurn, AtalyaSamiteMaster))
        return d
    }

    fun solver(): ManaSolver {
        val registry = CardRegistry()
        registry.register(TestCards.all + listOf(SoulBurn, AtalyaSamiteMaster))
        return ManaSolver(registry)
    }

    test("Soul Burn: life gained equals the black mana spent on X") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 20), startingLife = 20)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val soulBurn = d.putCardInHand(me, "Soul Burn")
        // {X}{2}{B} with X=3 paid entirely with black: base {2}{B} = 3 black, X = 3 black.
        d.giveMana(me, Color.BLACK, 6)
        d.castXSpell(me, soulBurn, xValue = 3, targets = listOf(opp))
        d.bothPass()

        // 3 damage to the opponent; 3 life gained (3 black mana spent on X).
        d.getLifeTotal(opp) shouldBe 17
        d.getLifeTotal(me) shouldBe 23
    }

    test("Soul Burn: X can be paid only with black or red mana") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 10, "Forest" to 10), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 2 black sources + 4 green sources. Base {2}{B} consumes the one mandatory black plus
        // two generic; at most one black source can be left for X, so X=2 can't be covered by the
        // black/red restriction even though the board has plenty of (green) mana for it.
        repeat(2) { d.putLandOnBattlefield(me, "Swamp") }
        repeat(4) { d.putLandOnBattlefield(me, "Forest") }

        val solver = solver()
        val cost = ManaCost.parse("{X}{2}{B}")
        val brOnly = setOf(Color.BLACK, Color.RED)

        // Without the restriction, X=2 is easily affordable from the green mana.
        solver.canPay(d.state, me, cost, xValue = 2).shouldBeTrue()
        // With "spend only black/red on X", the green mana can't pay X → not affordable.
        solver.canPay(d.state, me, cost, xValue = 2, xManaRestriction = brOnly).shouldBeFalse()
    }

    test("Atalya: X can be paid only with white mana") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 10, "Forest" to 10), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // One white source, three green. Atalya's {X} portion must be white only.
        d.putLandOnBattlefield(me, "Plains")
        repeat(3) { d.putLandOnBattlefield(me, "Forest") }

        val solver = solver()
        val xCost = ManaCost.parse("{X}")
        val whiteOnly = setOf(Color.WHITE)

        // X=1 is payable with the single white source.
        solver.canPay(d.state, me, xCost, xValue = 1, xManaRestriction = whiteOnly).shouldBeTrue()
        // X=2 needs a second white source — the green mana can't pay it.
        solver.canPay(d.state, me, xCost, xValue = 2, xManaRestriction = whiteOnly).shouldBeFalse()
        // Without the restriction, X=2 is affordable from any two lands.
        solver.canPay(d.state, me, xCost, xValue = 2).shouldBeTrue()
    }
})
