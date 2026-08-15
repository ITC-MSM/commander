package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.TerritoryForge
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Territory Forge — {4}{R} Artifact (BIG #15).
 *
 * "When this artifact enters, if you cast it, exile target artifact or land.
 *  This artifact has all activated abilities of the exiled card."
 */
class TerritoryForgeScenarioTest : FunSpec({

    // A test artifact whose only ability is "{T}: You gain 2 life" — the activated ability we
    // expect Territory Forge to inherit once it exiles this card.
    val gainLifeArtifact = card("Forge Test Lamp") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "{T}: You gain 2 life."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(2)
        }
    }
    val gainLifeAbilityId = gainLifeArtifact.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TerritoryForge, gainLifeArtifact))
        return driver
    }

    test("cast Territory Forge → exile an artifact → activate the exiled card's {T} ability on the Forge") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // The artifact to exile and inherit abilities from.
        val lamp = driver.putPermanentOnBattlefield(me, "Forge Test Lamp")

        // Cast Territory Forge (so the "if you cast it" intervening-if is satisfied).
        val forge = driver.putCardInHand(me, "Territory Forge")
        driver.giveColorlessMana(me, 4)
        driver.giveMana(me, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(me, forge).isSuccess shouldBe true
        driver.bothPass() // resolve Territory Forge → it enters, ETB trigger goes on the stack and prompts a target

        // The ETB asks for "target artifact or land" — choose the lamp.
        driver.submitTargetSelection(me, listOf(lamp))
        driver.bothPass() // resolve the ETB → exile the lamp linked to the Forge

        // The lamp is exiled; Territory Forge is on the battlefield.
        driver.state.getZone(me, Zone.BATTLEFIELD).contains(lamp) shouldBe false
        driver.state.getZone(me, Zone.EXILE).contains(lamp) shouldBe true
        driver.state.getZone(me, Zone.BATTLEFIELD).contains(forge) shouldBe true

        // Activate the inherited "{T}: You gain 2 life" ability ON Territory Forge.
        val lifeBefore = driver.getLifeTotal(me)
        driver.submit(ActivateAbility(playerId = me, sourceId = forge, abilityId = gainLifeAbilityId))
            .isSuccess shouldBe true
        driver.bothPass() // resolve the gain-life

        driver.getLifeTotal(me) shouldBe lifeBefore + 2
    }

    test("if Territory Forge is NOT cast, the ETB does nothing (no exile, no inherited abilities)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lamp = driver.putPermanentOnBattlefield(me, "Forge Test Lamp")

        // Put Territory Forge directly onto the battlefield (not cast) — the "if you cast it"
        // intervening-if is false, so the ETB trigger does nothing.
        val forge = driver.putPermanentOnBattlefield(me, "Territory Forge")
        driver.bothPass()

        // No target decision should be pending and the lamp stays on the battlefield.
        driver.state.getZone(me, Zone.BATTLEFIELD).contains(lamp) shouldBe true
        driver.state.getZone(me, Zone.EXILE).contains(lamp) shouldBe false

        // With nothing exiled, the inherited ability isn't available — activating it fails.
        driver.submit(ActivateAbility(playerId = me, sourceId = forge, abilityId = gainLifeAbilityId))
            .isSuccess shouldBe false
    }
})
