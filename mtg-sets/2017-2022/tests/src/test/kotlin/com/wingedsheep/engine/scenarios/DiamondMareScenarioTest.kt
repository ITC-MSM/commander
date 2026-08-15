package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.m19.cards.DiamondMare
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Diamond Mare (M19 #231, reprinted in Foundations) — "As this creature enters, choose a
 * color. Whenever you cast a spell of the chosen color, you gain 1 life."
 *
 * Verifies the `youCastSpell` trigger filtered by
 * [com.wingedsheep.sdk.scripting.GameObjectFilter.sharingChosenColorWithSource]: the spell
 * only pays off when its color includes the color stored on Diamond Mare. The chosen color
 * is injected directly (the EntersWithChoice(COLOR) → CastChoicesComponent path is covered
 * elsewhere, e.g. HarshJudgmentTest).
 */
class DiamondMareScenarioTest : FunSpec({

    fun newGame(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DiamondMare))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!
        return Triple(driver, active, driver.getOpponent(active))
    }

    fun GameTestDriver.lifeOf(playerId: EntityId): Int =
        state.getEntity(playerId)?.get<LifeTotalComponent>()?.life ?: 0

    test("gains life when you cast a spell of the chosen color") {
        val (driver, active, defender) = newGame()
        val mare = driver.putPermanentOnBattlefield(active, "Diamond Mare")
        driver.addComponent(mare, CastChoicesComponent(chosen = mapOf(ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.RED))))

        // Active player casts a red spell (Lightning Bolt) — matches the chosen color.
        driver.giveMana(active, Color.RED, 1)
        val bolt = driver.putCardInHand(active, "Lightning Bolt")
        driver.castSpellWithTargets(active, bolt, listOf(ChosenTarget.Player(defender)))
        driver.bothPass()

        driver.lifeOf(active) shouldBe 21
    }

    test("does not gain life when the spell is a different color") {
        val (driver, active, defender) = newGame()
        val mare = driver.putPermanentOnBattlefield(active, "Diamond Mare")
        driver.addComponent(mare, CastChoicesComponent(chosen = mapOf(ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.BLUE))))

        // Red bolt is not the chosen color (blue) — no life gain.
        driver.giveMana(active, Color.RED, 1)
        val bolt = driver.putCardInHand(active, "Lightning Bolt")
        driver.castSpellWithTargets(active, bolt, listOf(ChosenTarget.Player(defender)))
        driver.bothPass()

        driver.lifeOf(active) shouldBe 20
    }
})
