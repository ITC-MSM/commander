package com.wingedsheep.engine.scenarios
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.FrostcliffSiege
import com.wingedsheep.mtg.sets.definitions.tdm.cards.GlacierwoodSiege
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Verifies that mode-gated *continuous static* abilities work — the dual-mode
 * SourceChosenModeIs condition must evaluate during projection (not only at resolution),
 * otherwise a ConditionalStaticAbility gated by it would never apply.
 *
 * - Frostcliff Siege (Temur): "Creatures you control get +1/+0 and have trample and haste."
 * - Glacierwood Siege (Sultai): "You may play lands from your graveyard."
 */
class SiegeStaticModeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FrostcliffSiege, GlacierwoodSiege))
        return driver
    }

    test("Frostcliff Temur grants +1/+0, trample, and haste to creatures you control") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val bear = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3

        val siege = driver.putPermanentOnBattlefield(you, "Frostcliff Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("temur"))))

        val projected = driver.state.projectedState
        projected.getPower(bear) shouldBe 4
        projected.hasKeyword(bear, Keyword.TRAMPLE) shouldBe true
        projected.hasKeyword(bear, Keyword.HASTE) shouldBe true
    }

    test("Frostcliff Jeskai mode does NOT grant the Temur lord bonuses") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val bear = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3

        val siege = driver.putPermanentOnBattlefield(you, "Frostcliff Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("jeskai"))))

        val projected = driver.state.projectedState
        projected.getPower(bear) shouldBe 3
        projected.hasKeyword(bear, Keyword.TRAMPLE) shouldBe false
    }

    test("Glacierwood Sultai lets you play a land from your graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val siege = driver.putPermanentOnBattlefield(you, "Glacierwood Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("sultai"))))

        // A land sitting in the graveyard.
        val land = driver.putCardInGraveyard(you, "Forest")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val landsBefore = driver.getLands(you).size
        driver.playLand(you, land)
        driver.getLands(you).size shouldBe landsBefore + 1
    }
})
