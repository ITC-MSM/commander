package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.EnduringCourage
import com.wingedsheep.mtg.sets.definitions.dsk.cards.EnduringInnocence
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for the Duskmourn "Enduring" mechanic (the Glimmer cycle).
 *
 * "When this permanent dies, if it was a creature, return it to the battlefield under its owner's
 * control. It's an enchantment. (It's not a creature.)"
 *
 * Verifies the return-as-enchantment behavior, that the returned enchantment loses its creature
 * type and subtypes, that it doesn't loop on a second death, and that tokens don't return.
 */
class EnduringMechanicTest : FunSpec({

    val projector = StateProjector()

    // Inline removal that can destroy any permanent (TestCards has no enchantment removal).
    val unmaking = card("Unmaking") {
        manaCost = "{1}{W}"
        typeLine = "Instant"
        spell {
            val t = target("permanent", Targets.Permanent)
            effect = MoveToZoneEffect(t, Zone.GRAVEYARD, byDestruction = true)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(EnduringInnocence, EnduringCourage, unmaking))
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            skipMulligans = true
        )
        return driver
    }

    test("Enduring Innocence returns as an enchantment with no creature type or subtypes when it dies") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Enduring Innocence")
        val innocence = driver.findPermanent(caster, "Enduring Innocence")
        innocence.shouldNotBeNull()
        projector.project(driver.state).isCreature(innocence) shouldBe true

        // Kill it with a Lightning Bolt (2/1 dies to 3 damage).
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt, listOf(innocence)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt → dies
        driver.bothPass() // resolve the Enduring return trigger

        val returned = driver.findPermanent(caster, "Enduring Innocence")
        returned.shouldNotBeNull()
        val projected = projector.project(driver.state)
        // It's an enchantment that is no longer a creature, with no creature subtypes.
        projected.isCreature(returned) shouldBe false
        projected.hasType(returned, "ENCHANTMENT") shouldBe true
        projected.hasSubtype(returned, "Sheep") shouldBe false
        projected.hasSubtype(returned, "Glimmer") shouldBe false
    }

    test("an Enduring permanent that already returned (now an enchantment) does not loop on its second death") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Enduring Courage")
        val courage = driver.findPermanent(caster, "Enduring Courage")
        courage.shouldNotBeNull()

        // First death: a 3/3 needs 4 damage; bolt it twice.
        driver.putCardInHand(caster, "Lightning Bolt").let { bolt ->
            driver.giveMana(caster, Color.RED, 1)
            driver.castSpell(caster, bolt, listOf(courage))
            driver.bothPass()
        }
        driver.putCardInHand(caster, "Lightning Bolt").let { bolt ->
            driver.giveMana(caster, Color.RED, 1)
            driver.castSpell(caster, bolt, listOf(courage))
            driver.bothPass() // lethal → dies
        }
        driver.bothPass() // resolve Enduring return

        val returned = driver.findPermanent(caster, "Enduring Courage")
        returned.shouldNotBeNull()
        projector.project(driver.state).isCreature(returned) shouldBe false

        // Destroy the enchantment. It is not a creature, so the Enduring trigger must NOT fire.
        val unmake = driver.putCardInHand(caster, "Unmaking")
        driver.giveMana(caster, Color.WHITE, 2)
        driver.castSpell(caster, unmake, listOf(returned)).isSuccess shouldBe true
        driver.bothPass() // resolve Unmaking → enchantment to graveyard
        driver.bothPass() // settle any triggers (there should be none from Enduring)

        // No second return: it stays in the graveyard.
        driver.findPermanent(caster, "Enduring Courage") shouldBe null
        driver.getGraveyardCardNames(caster) shouldContain "Enduring Courage"
    }

    test("a token copy of an Enduring creature does not return (CR 111.7)") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Enduring Innocence")
        val tokenId = driver.findPermanent(caster, "Enduring Innocence")
        tokenId.shouldNotBeNull()
        driver.replaceState(driver.state.updateEntity(tokenId) { c -> c.with(TokenComponent) })

        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt, listOf(tokenId)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt → token dies and ceases to exist

        driver.findPermanent(caster, "Enduring Innocence") shouldBe null
        driver.state.getBattlefield().contains(tokenId) shouldBe false
    }
})
