package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.HauntingWind
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Haunting Wind — {3}{B} Enchantment.
 *
 * "Whenever an artifact becomes tapped or a player activates an artifact's ability without {T} in
 *  its activation cost, this enchantment deals 1 damage to that artifact's controller."
 */
class HauntingWindScenarioTest : FunSpec({

    // Artifact with a {T}: Add {C} mana ability — activating it taps the artifact (tap half).
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

    // Artifact with a non-{T} ability ("{1}: you gain 1 life") — activating it fires the ability half.
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
        listOf(HauntingWind, tapRock, pingRock).forEach { driver.registerCard(it) }
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 30) { bothPass(); guard++ }
    }

    test("tapping an artifact deals 1 to that artifact's controller") {
        val driver = createDriver()
        val me = driver.player1

        driver.putPermanentOnBattlefield(me, "Haunting Wind")
        val rock = driver.putPermanentOnBattlefield(me, "Tap Rock")

        val lifeBefore = driver.getLifeTotal(me)
        val abilityId = driver.cardRegistry.requireCard("Tap Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        // Tap half fired: 1 damage to the artifact's controller (me). The {T} ability half does NOT.
        driver.getLifeTotal(me) shouldBe lifeBefore - 1
    }

    test("activating an artifact's non-{T} ability deals 1 to that artifact's controller") {
        val driver = createDriver()
        val me = driver.player1

        driver.putPermanentOnBattlefield(me, "Haunting Wind")
        val rock = driver.putPermanentOnBattlefield(me, "Ping Rock")

        val lifeBefore = driver.getLifeTotal(me)
        driver.giveColorlessMana(me, 1)
        val abilityId = driver.cardRegistry.requireCard("Ping Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        // Ability half deals 1; the ability itself gains 1. Net 0; the artifact never tapped.
        driver.getLifeTotal(me) shouldBe lifeBefore
    }

    test("global + 'that artifact's controller': Haunting Wind under the opponent still damages the artifact's controller") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        // Opponent controls Haunting Wind; I control (and tap) the artifact. The trigger is global
        // ("an artifact becomes tapped"), and the damage goes to the artifact's controller — me —
        // not to Haunting Wind's controller.
        driver.putPermanentOnBattlefield(opp, "Haunting Wind")
        val rock = driver.putPermanentOnBattlefield(me, "Tap Rock")

        val myLifeBefore = driver.getLifeTotal(me)
        val oppLifeBefore = driver.getLifeTotal(opp)
        val abilityId = driver.cardRegistry.requireCard("Tap Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        driver.getLifeTotal(me) shouldBe myLifeBefore - 1
        driver.getLifeTotal(opp) shouldBe oppLifeBefore
    }
})
