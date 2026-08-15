package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.ArtifactPossession
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Artifact Possession — {2}{B} Enchantment — Aura, "Enchant artifact".
 *
 * "Whenever enchanted artifact becomes tapped or a player activates an ability of enchanted
 *  artifact without {T} in its activation cost, this Aura deals 2 damage to that artifact's
 *  controller."
 *
 * Single-target ([TriggerBinding.ATTACHED]) member of the Antiquities tap/activate punisher family:
 * only the enchanted artifact fires it (an un-enchanted artifact does nothing).
 */
class ArtifactPossessionScenarioTest : FunSpec({

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
        listOf(ArtifactPossession, tapRock, pingRock).forEach { driver.registerCard(it) }
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 30) { bothPass(); guard++ }
    }

    // Put the artifact in play and cast Artifact Possession onto it (Aura attaches on resolution).
    fun GameTestDriver.enchant(player: com.wingedsheep.sdk.model.EntityId, rockName: String): com.wingedsheep.sdk.model.EntityId {
        val rock = putPermanentOnBattlefield(player, rockName)
        val aura = putCardInHand(player, "Artifact Possession")
        giveColorlessMana(player, 2)
        giveMana(player, Color.BLACK, 1)
        castSpell(player, aura, listOf(rock))
        bothPass() // resolve the Aura
        findPermanent(player, "Artifact Possession") shouldNotBe null
        return rock
    }

    test("tapping the enchanted artifact deals 2 to that artifact's controller") {
        val driver = createDriver()
        val me = driver.player1

        val rock = driver.enchant(me, "Tap Rock")

        val lifeBefore = driver.getLifeTotal(me)
        val abilityId = driver.cardRegistry.requireCard("Tap Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        driver.getLifeTotal(me) shouldBe lifeBefore - 2
    }

    test("activating the enchanted artifact's non-{T} ability deals 2 to that artifact's controller") {
        val driver = createDriver()
        val me = driver.player1

        val rock = driver.enchant(me, "Ping Rock")

        val lifeBefore = driver.getLifeTotal(me)
        driver.giveColorlessMana(me, 1)
        val abilityId = driver.cardRegistry.requireCard("Ping Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = rock, abilityId = abilityId))
        driver.resolveStack()

        // Ability half deals 2; the ability gains 1. Net -1.
        driver.getLifeTotal(me) shouldBe lifeBefore - 1
    }

    test("an un-enchanted artifact does NOT fire Artifact Possession") {
        val driver = createDriver()
        val me = driver.player1

        // Enchant one rock, then tap a DIFFERENT, un-enchanted rock — no trigger.
        driver.enchant(me, "Tap Rock")
        val other = driver.putPermanentOnBattlefield(me, "Ping Rock")

        val lifeBefore = driver.getLifeTotal(me)
        driver.giveColorlessMana(me, 1)
        val abilityId = driver.cardRegistry.requireCard("Ping Rock").activatedAbilities[0].id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = other, abilityId = abilityId))
        driver.resolveStack()

        // Only the un-enchanted rock's own +1 life; Artifact Possession (on the other rock) is silent.
        driver.getLifeTotal(me) shouldBe lifeBefore + 1
    }
})
