package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.PrismariTheInspiration
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.StormCopyEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Prismari, the Inspiration: "Instant and sorcery spells you cast have storm." The grant is a
 * [com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells] static ability read by the cast handler
 * via [com.wingedsheep.engine.mechanics.mana.GrantedKeywordResolver]. A non-storm instant cast
 * while Prismari is in play should produce a storm trigger; two Prismaris should produce two.
 */
class PrismariTheInspirationScenarioTest : FunSpec({

    fun stormTriggers(driver: GameTestDriver): List<StormCopyEffect> =
        driver.state.stack.mapNotNull {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()
        }.mapNotNull { it.effect as? StormCopyEffect }

    test("instant cast with Prismari in play gets a storm trigger with copyCount = spells cast before it") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PrismariTheInspiration))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        driver.putCreatureOnBattlefield(caster, "Prismari, the Inspiration")
        driver.replaceState(driver.state.copy(spellsCastThisTurn = 2))
        driver.putLandOnBattlefield(caster, "Mountain")
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")

        driver.castSpell(caster, bolt, listOf(opponent)).isSuccess shouldBe true

        val triggers = stormTriggers(driver)
        triggers.size shouldBe 1
        triggers.single().copyCount shouldBe 2
    }

    test("two Prismaris produce two separate storm triggers (CR 702.40b)") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PrismariTheInspiration))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        driver.putCreatureOnBattlefield(caster, "Prismari, the Inspiration")
        driver.putCreatureOnBattlefield(caster, "Prismari, the Inspiration")
        driver.replaceState(driver.state.copy(spellsCastThisTurn = 1))
        driver.putLandOnBattlefield(caster, "Mountain")
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")

        driver.castSpell(caster, bolt, listOf(opponent)).isSuccess shouldBe true

        stormTriggers(driver).size shouldBe 2
    }

    test("without Prismari, an instant produces no storm trigger") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PrismariTheInspiration))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        driver.replaceState(driver.state.copy(spellsCastThisTurn = 2))
        driver.putLandOnBattlefield(caster, "Mountain")
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")

        driver.castSpell(caster, bolt, listOf(opponent)).isSuccess shouldBe true

        stormTriggers(driver).size shouldBe 0
    }
})
