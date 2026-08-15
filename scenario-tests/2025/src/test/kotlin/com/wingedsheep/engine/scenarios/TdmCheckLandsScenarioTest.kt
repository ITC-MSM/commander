package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.GreatArashinCity
import com.wingedsheep.mtg.sets.definitions.tdm.cards.MistriseVillage
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for the two TDM check-lands Great Arashin City (#257) and Mistrise Village (#261), plus
 * Great Arashin City's Spirit-token ability.
 */
class TdmCheckLandsScenarioTest : FunSpec({

    val testBear = CardDefinition.creature(
        name = "Test Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(com.wingedsheep.sdk.core.Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    fun GameTestDriver.countPermanents(name: String): Int =
        state.getBattlefield().count { state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == name }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + GreatArashinCity + MistriseVillage + testBear)
        return driver
    }

    test("Great Arashin City enters untapped when you control a Forest") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putLandOnBattlefield(p1, "Forest")
        val city = driver.putCardInHand(p1, "Great Arashin City")
        driver.playLand(p1, city).isSuccess shouldBe true

        driver.state.getEntity(city)?.has<TappedComponent>() shouldBe false
    }

    test("Great Arashin City enters tapped with no Forest or Plains") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val city = driver.putCardInHand(p1, "Great Arashin City")
        driver.playLand(p1, city).isSuccess shouldBe true

        driver.state.getEntity(city)?.has<TappedComponent>() shouldBe true
    }

    test("Great Arashin City: exiling a creature from graveyard creates a 1/1 Spirit") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val city = driver.putPermanentOnBattlefield(p1, "Great Arashin City")
        driver.putCardInGraveyard(p1, "Test Bear")
        driver.giveMana(p1, Color.BLACK, 2)

        val spiritAbility = GreatArashinCity.activatedAbilities[1].id
        driver.submit(ActivateAbility(p1, city, spiritAbility)).isSuccess shouldBe true
        var guard = 0
        while (driver.state.pendingDecision != null && guard < 10) { driver.autoResolveDecision(); guard++ }
        var stackGuard = 0
        while (driver.state.stack.isNotEmpty() && stackGuard < 20) { driver.bothPass(); stackGuard++ }

        driver.countPermanents("Spirit Token") shouldBe 1
    }

    test("Mistrise Village enters untapped when you control a Mountain") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putLandOnBattlefield(p1, "Mountain")
        val village = driver.putCardInHand(p1, "Mistrise Village")
        driver.playLand(p1, village).isSuccess shouldBe true

        driver.state.getEntity(village)?.has<TappedComponent>() shouldBe false
    }

    test("Mistrise Village taps for blue") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val village = driver.putPermanentOnBattlefield(p1, "Mistrise Village")
        val manaAbility = MistriseVillage.activatedAbilities[0].id
        driver.submit(ActivateAbility(p1, village, manaAbility)).isSuccess shouldBe true

        driver.state.getEntity(p1)?.get<ManaPoolComponent>()?.blue shouldBe 1
    }
})
