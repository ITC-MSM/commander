package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gpt.cards.Frazzle
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Frazzle: Counter target nonblue spell. The notColor(BLUE) filter must let a nonblue spell on the
 * stack be countered while rejecting a blue spell as an illegal target.
 *
 * Both spells are cast by the active player, who retains priority after the first cast and then
 * targets it with Frazzle — this keeps the targeted spell on the stack without needing the
 * non-active player to gain priority.
 */
class FrazzleScenarioTest : FunSpec({

    // A no-target blue instant, so we can put a blue spell on the stack to prove Frazzle can't hit it.
    val blueGainer = card("Azure Gainer") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(1) }
    }

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Frazzle)
        driver.registerCard(blueGainer)
        driver.initMirrorMatch(deck = Deck.of("Island" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun spellOnStackNamed(driver: GameTestDriver, name: String) =
        driver.state.stack.first { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.name == name
        }

    test("Frazzle counters a nonblue (red) spell on the stack") {
        val driver = setup()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        // Active player casts a red spell (Lightning Bolt) at the opponent, retains priority.
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpellWithTargets(player, bolt, listOf(ChosenTarget.Player(opponent))).isSuccess shouldBe true
        val boltOnStack = spellOnStackNamed(driver, "Lightning Bolt")

        // ...and responds with Frazzle, countering the nonblue Bolt.
        val frazzle = driver.putCardInHand(player, "Frazzle")
        driver.giveMana(player, Color.BLUE, 4) // {3}{U}
        driver.castSpellWithTargets(player, frazzle, listOf(ChosenTarget.Spell(boltOnStack))).isSuccess shouldBe true

        driver.bothPass()
        driver.bothPass()

        // Bolt is countered → opponent never took damage and the Bolt is in the graveyard.
        driver.getLifeTotal(opponent) shouldBe 20
        driver.getGraveyardCardNames(player).contains("Lightning Bolt") shouldBe true
    }

    test("Frazzle cannot target a blue spell") {
        val driver = setup()
        val player = driver.activePlayer!!

        // Active player casts a blue instant, retains priority.
        val blue = driver.putCardInHand(player, "Azure Gainer")
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, blue).isSuccess shouldBe true
        val blueOnStack = spellOnStackNamed(driver, "Azure Gainer")

        val frazzle = driver.putCardInHand(player, "Frazzle")
        driver.giveMana(player, Color.BLUE, 4)
        val result = driver.castSpellWithTargets(player, frazzle, listOf(ChosenTarget.Spell(blueOnStack)))
        result.isSuccess shouldBe false
    }
})
