package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Vigor Mortis — reanimation from your own graveyard, with an extra +1/+1 counter if {G} was among
 * the mana paid.
 */
class VigorMortisScenarioTest : FunSpec({

    test("returns the creature with a +1/+1 counter when green mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val mortis = driver.putCardInHand(caster, "Vigor Mortis")
        val bears = driver.putCardInGraveyard(caster, "Grizzly Bears")

        // {2}{B}{B} paid as two black plus two green: green covers the generic.
        driver.giveMana(caster, Color.BLACK, 2)
        driver.giveMana(caster, Color.GREEN, 2)

        driver.castSpellWithTargets(
            caster,
            mortis,
            listOf(ChosenTarget.Card(cardId = bears, ownerId = caster, zone = Zone.GRAVEYARD)),
        ).error shouldBe null
        driver.bothPass()

        driver.assertPermanentExists(caster, "Grizzly Bears")
        val returned = driver.findPermanent(caster, "Grizzly Bears")!!
        driver.state.getEntity(returned)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        driver.state.projectedState.getPower(returned) shouldBe 3
        driver.state.projectedState.getToughness(returned) shouldBe 3
    }

    test("returns the creature with no counter when no green mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val mortis = driver.putCardInHand(caster, "Vigor Mortis")
        val bears = driver.putCardInGraveyard(caster, "Grizzly Bears")

        driver.giveMana(caster, Color.BLACK, 4)

        driver.castSpellWithTargets(
            caster,
            mortis,
            listOf(ChosenTarget.Card(cardId = bears, ownerId = caster, zone = Zone.GRAVEYARD)),
        ).error shouldBe null
        driver.bothPass()

        driver.assertPermanentExists(caster, "Grizzly Bears")
        val returned = driver.findPermanent(caster, "Grizzly Bears")!!
        driver.state.getEntity(returned)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0 shouldBe 0
        driver.state.projectedState.getPower(returned) shouldBe 2
    }
})
