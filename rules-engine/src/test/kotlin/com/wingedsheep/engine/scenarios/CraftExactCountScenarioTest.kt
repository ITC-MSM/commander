package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import com.wingedsheep.engine.core.Outcome
import io.kotest.matchers.shouldNotBe

/**
 * Exact-count Craft costs (CR 702.167a): "Craft with artifact" exiles exactly one material —
 * costs are paid exactly as written, so supplying more materials than [maxCount] must be
 * rejected, exactly like supplying fewer than [minCount].
 *
 * Uses a test-local DFC with `craft(filter = artifact, maxCount = 1)` so the check is
 * independent of any particular set's card.
 */
class CraftExactCountScenarioTest : FunSpec({

    val artifactFilter = GameObjectFilter.Artifact

    val frontFace = card("Test Exact Crafter") {
        manaCost = "{1}"
        typeLine = "Artifact"
        oracleText = "Craft with artifact {2}"
        craft(filter = artifactFilter, cost = "{2}", materialDescription = "artifact", minCount = 1, maxCount = 1)
    }
    val backFace = card("Test Crafted Golem") {
        manaCost = ""
        typeLine = "Artifact Creature — Golem"
        power = 3
        toughness = 3
        oracleText = ""
    }
    val exactCrafter: CardDefinition = CardDefinition.doubleFacedPermanent(
        frontFace = frontFace,
        backFace = backFace
    )

    val trinket = card("Test Trinket") {
        manaCost = "{1}"
        typeLine = "Artifact"
        oracleText = ""
    }

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(exactCrafter, trinket))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        return driver
    }

    fun craftAbilityId() = exactCrafter.activatedAbilities.single().id

    test("accepts exactly one material and returns transformed") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val crafter = driver.putPermanentOnBattlefield(p1, "Test Exact Crafter")
        val material = driver.putPermanentOnBattlefield(p1, "Test Trinket")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.RED, 2)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = crafter,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(material))
            )
        )
        driver.bothPass()

        driver.state.getZone(ZoneKey(p1, Zone.EXILE)).contains(material) shouldBe true
        val card = driver.state.getEntity(crafter)?.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Test Crafted Golem"
    }

    test("rejects supplying two materials when the craft cost is exactly one (CR 118.8)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val crafter = driver.putPermanentOnBattlefield(p1, "Test Exact Crafter")
        val material1 = driver.putPermanentOnBattlefield(p1, "Test Trinket")
        val material2 = driver.putPermanentOnBattlefield(p1, "Test Trinket")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.RED, 2)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = crafter,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(material1, material2))
            )
        )
        result.outcome shouldNotBe Outcome.Done
        result.error.shouldNotBeNull() shouldContain "at most"

        // Nothing moved: both materials and the crafter are still on the battlefield.
        val battlefield = driver.state.getZone(ZoneKey(p1, Zone.BATTLEFIELD))
        battlefield.contains(crafter) shouldBe true
        battlefield.contains(material1) shouldBe true
        battlefield.contains(material2) shouldBe true
    }
})
