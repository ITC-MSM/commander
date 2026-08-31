package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dryad's Caress — life for every creature on the battlefield (not just yours), and an untap of your
 * own creatures if {W} was among the mana paid.
 */
class DryadsCaressScenarioTest : FunSpec({

    test("gains life for every creature on the battlefield and untaps yours when white mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val caress = driver.putCardInHand(caster, "Dryad's Caress")

        val mine = driver.putCreatureOnBattlefield(caster, "Grizzly Bears")
        val alsoMine = driver.putCreatureOnBattlefield(caster, "Grizzly Bears")
        val theirs = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.tapPermanent(mine)
        driver.tapPermanent(alsoMine)
        driver.tapPermanent(theirs)

        // {4}{G}{G} paid as two green plus four white: white covers the generic.
        driver.giveMana(caster, Color.GREEN, 2)
        driver.giveMana(caster, Color.WHITE, 4)

        driver.castSpell(caster, caress).error shouldBe null
        driver.bothPass()

        // Three creatures on the battlefield, whoever controls them.
        driver.assertLifeTotal(caster, 23)
        driver.isTapped(mine) shouldBe false
        driver.isTapped(alsoMine) shouldBe false
        // "Creatures you control" — the opponent's stays tapped.
        driver.isTapped(theirs) shouldBe true
    }

    test("gains the life but untaps nothing when no white mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val caress = driver.putCardInHand(caster, "Dryad's Caress")
        val mine = driver.putCreatureOnBattlefield(caster, "Grizzly Bears")
        driver.tapPermanent(mine)

        driver.giveMana(caster, Color.GREEN, 6)

        driver.castSpell(caster, caress).error shouldBe null
        driver.bothPass()

        driver.assertLifeTotal(caster, 21)
        driver.isTapped(mine) shouldBe true
    }
})
