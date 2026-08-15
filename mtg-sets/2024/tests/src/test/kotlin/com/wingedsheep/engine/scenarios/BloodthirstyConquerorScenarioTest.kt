package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.BloodthirstyConqueror
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bloodthirsty Conqueror {3}{B}{B} — Creature — Vampire Knight 5/5
 *   Flying, deathtouch
 *   Whenever an opponent loses life, you gain that much life.
 *
 * Proves the new [com.wingedsheep.sdk.dsl.Triggers.AnOpponentLosesLife] trigger fires on both
 * combat and non-combat life loss, and that the controller gains exactly the amount lost.
 */
class BloodthirstyConquerorScenarioTest : FunSpec({

    // A cheap instant that makes each opponent lose 2 life — a non-combat life-loss source.
    val DrainTwo = CardDefinition.instant(
        name = "Drain Two",
        manaCost = ManaCost.parse("{B}"),
        oracleText = "Each opponent loses 2 life.",
        script = CardScript.spell(
            effect = LoseLifeEffect(
                amount = DynamicAmount.Fixed(2),
                target = EffectTarget.PlayerRef(Player.EachOpponent)
            )
        )
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BloodthirstyConqueror, DrainTwo))
        return driver
    }

    test("combat damage: opponent loses 5, you gain 5") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val conqueror = driver.putCreatureOnBattlefield(you, "Bloodthirsty Conqueror")
        driver.removeSummoningSickness(conqueror)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(conqueror), defendingPlayer = opponent).error shouldBe null

        // No blockers (opponent has none) — resolve combat damage, then the life-gain trigger.
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.bothPass() // combat damage: opponent 20 -> 15
        driver.bothPass() // resolve "you gain that much life"

        driver.assertLifeTotal(opponent, 15)
        driver.assertLifeTotal(you, 25)
    }

    test("non-combat life loss: opponent loses 2, you gain exactly 2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Bloodthirsty Conqueror")

        val spell = driver.putCardInHand(you, "Drain Two")
        driver.giveMana(you, Color.BLACK, 1)
        driver.castSpell(you, spell).isSuccess shouldBe true
        driver.bothPass() // resolve Drain Two: opponent 20 -> 18
        driver.bothPass() // resolve the life-gain trigger

        driver.assertLifeTotal(opponent, 18)
        driver.assertLifeTotal(you, 22)
    }
})
