package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ribbons of Night — 4 damage to a creature plus 4 life, and a card if {U} was among the mana paid.
 *
 * The two tests differ only in which mana is in the pool when the spell is cast, so they pin the
 * rider to the *payment* rather than to the spell's colour.
 */
class RibbonsOfNightScenarioTest : FunSpec({

    test("draws a card when blue mana was spent to cast it") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val ribbons = driver.putCardInHand(caster, "Ribbons of Night")
        val victim = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // {4}{B} paid as one black plus four blue: blue covers the generic, so blue was spent.
        driver.giveMana(caster, Color.BLACK, 1)
        driver.giveMana(caster, Color.BLUE, 4)

        val handBefore = driver.getHandSize(caster)
        driver.castSpellWithTargets(caster, ribbons, listOf(ChosenTarget.Permanent(victim)))
            .error shouldBe null
        driver.bothPass()

        driver.assertLifeTotal(caster, 24)
        driver.assertInGraveyard(opponent, "Grizzly Bears")
        // One card leaves the hand (the spell itself) and one is drawn by the rider.
        driver.getHandSize(caster) shouldBe handBefore
    }

    test("draws no card when no blue mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val ribbons = driver.putCardInHand(caster, "Ribbons of Night")
        val victim = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.giveMana(caster, Color.BLACK, 5)

        val handBefore = driver.getHandSize(caster)
        driver.castSpellWithTargets(caster, ribbons, listOf(ChosenTarget.Permanent(victim)))
            .error shouldBe null
        driver.bothPass()

        driver.assertLifeTotal(caster, 24)
        driver.assertInGraveyard(opponent, "Grizzly Bears")
        driver.getHandSize(caster) shouldBe handBefore - 1
    }
})
