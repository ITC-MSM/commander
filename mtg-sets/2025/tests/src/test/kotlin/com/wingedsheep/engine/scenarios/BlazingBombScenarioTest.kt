package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Blazing Bomb (FIN #130) — {R} 1/1 Elemental.
 *
 * "Whenever you cast a noncreature spell, if at least four mana was spent to cast it, put a +1/+1
 *  counter on this creature.
 *  Blow Up — {T}, Sacrifice this creature: It deals damage equal to its power to target creature.
 *  Activate only as a sorcery."
 *
 * Proves the cast trigger's intervening-if (≥4 mana) and that Blow Up reads the Bomb's *last-known*
 * power after it sacrifices itself (CR 112.7a / 608.2h) — so a Bomb pumped to 2 power kills a 2/2,
 * rather than dealing the pre-fix 0 (or its printed 1).
 */
class BlazingBombScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun resolveStack(d: GameTestDriver) {
        var guard = 0
        while (d.state.stack.isNotEmpty() && guard < 30) {
            d.bothPass()
            guard++
        }
    }

    test("casting a 5-mana noncreature spell adds a +1/+1 counter; a 3-mana one does not") {
        val d = driver()
        val me = d.player1
        val foe = d.player2

        val bomb = d.putCreatureOnBattlefield(me, "Blazing Bomb")

        // Cast Zap ({2}{R} = 3 mana): below the threshold, so no counter.
        val zap = d.putCardInHand(me, "Zap")
        d.giveMana(me, Color.RED, 1)
        d.giveColorlessMana(me, 2)
        d.castSpell(me, zap, listOf(foe)).isSuccess shouldBe true
        resolveStack(d)
        d.state.projectedState.getPower(bomb) shouldBe 1 // still 1/1 — trigger did not fire

        // Cast Lava Axe ({4}{R} = 5 mana): at/above the threshold, so +1/+1.
        val lavaAxe = d.putCardInHand(me, "Lava Axe")
        d.giveMana(me, Color.RED, 1)
        d.giveColorlessMana(me, 4)
        d.castSpell(me, lavaAxe, listOf(foe)).isSuccess shouldBe true
        resolveStack(d)
        d.state.projectedState.getPower(bomb) shouldBe 2 // now a 2/2
    }

    test("Blow Up: sacrifices the Bomb and deals damage equal to its (buffed) power") {
        val d = driver()
        val me = d.player1
        val foe = d.player2

        val bomb = d.putCreatureOnBattlefield(me, "Blazing Bomb")
        d.removeSummoningSickness(bomb)

        // Pump the Bomb to 2/2 by casting a 5-mana noncreature spell.
        val lavaAxe = d.putCardInHand(me, "Lava Axe")
        d.giveMana(me, Color.RED, 1)
        d.giveColorlessMana(me, 4)
        d.castSpell(me, lavaAxe, listOf(foe)).isSuccess shouldBe true
        resolveStack(d)
        d.state.projectedState.getPower(bomb) shouldBe 2

        // A 2/2 victim: it dies only to ≥2 damage, so this distinguishes the buffed power (2) from
        // the printed power (1) and from the pre-fix bug (0).
        val victim = d.putCreatureOnBattlefield(foe, "Grizzly Bears") // 2/2

        val blowUp = d.cardRegistry.requireCard("Blazing Bomb").activatedAbilities[0].id
        d.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = bomb,
                abilityId = blowUp,
                targets = listOf(ChosenTarget.Permanent(victim))
            )
        )
        resolveStack(d)

        // The Bomb sacrificed itself to pay the cost…
        d.getGraveyard(me).any { d.getCardName(it) == "Blazing Bomb" } shouldBe true
        // …and dealt 2 damage (its last-known power) to the 2/2, killing it.
        d.findPermanent(foe, "Grizzly Bears") shouldBe null
    }
})
