package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.Powerleech
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Powerleech — {G}{G} Enchantment.
 *
 * "Whenever an artifact an opponent controls becomes tapped or an opponent activates an artifact's
 *  ability without {T} in its activation cost, you gain 1 life."
 *
 * The opponent-controlled filter is what distinguishes Powerleech from Haunting Wind. Here player2
 * controls Powerleech and player1 (the active player, and player2's opponent) owns/taps the
 * artifacts — so player1's artifact activity is exactly "an opponent's artifact" from player2's
 * perspective, and the leech rewards player2.
 */
class PowerleechScenarioTest : FunSpec({

    val tapRock = card("Tap Rock") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "{T}: Add {C}."
        activatedAbility {
            cost = AbilityCost.Tap
            effect = AddColorlessManaEffect(1)
            manaAbility = true
        }
    }

    val pingRock = card("Ping Rock") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "{1}: You gain 1 life."
        activatedAbility {
            cost = com.wingedsheep.sdk.dsl.Costs.Mana("{1}")
            effect = Effects.GainLife(1)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        listOf(Powerleech, tapRock, pingRock).forEach { driver.registerCard(it) }
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 30) { bothPass(); guard++ }
    }

    test("an opponent tapping an artifact gains the leech's controller 1 life") {
        val driver = createDriver()
        val active = driver.player1   // opponent (relative to Powerleech's controller)
        val leecher = driver.player2  // controls Powerleech

        driver.putPermanentOnBattlefield(leecher, "Powerleech")
        val rock = driver.putPermanentOnBattlefield(active, "Tap Rock")

        val leecherLifeBefore = driver.getLifeTotal(leecher)
        val abilityId = driver.cardRegistry.requireCard("Tap Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = active, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        driver.getLifeTotal(leecher) shouldBe leecherLifeBefore + 1
    }

    test("an opponent activating an artifact's non-{T} ability gains the leech's controller 1 life") {
        val driver = createDriver()
        val active = driver.player1
        val leecher = driver.player2

        driver.putPermanentOnBattlefield(leecher, "Powerleech")
        val rock = driver.putPermanentOnBattlefield(active, "Ping Rock")

        val leecherLifeBefore = driver.getLifeTotal(leecher)
        driver.giveColorlessMana(active, 1)
        val abilityId = driver.cardRegistry.requireCard("Ping Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = active, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        driver.getLifeTotal(leecher) shouldBe leecherLifeBefore + 1
    }

    test("the controller's OWN artifact activity does NOT trigger Powerleech") {
        val driver = createDriver()
        val me = driver.player1 // controls both Powerleech and the rock

        driver.putPermanentOnBattlefield(me, "Powerleech")
        val rock = driver.putPermanentOnBattlefield(me, "Tap Rock")

        val lifeBefore = driver.getLifeTotal(me)
        val abilityId = driver.cardRegistry.requireCard("Tap Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        // My own artifact isn't "an artifact an opponent controls" — no life gained.
        driver.getLifeTotal(me) shouldBe lifeBefore
    }
})
