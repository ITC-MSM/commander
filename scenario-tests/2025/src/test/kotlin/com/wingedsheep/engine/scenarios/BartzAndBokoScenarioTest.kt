package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bartz and Boko (FIN #175) — {3}{G}{G} Legendary Creature — Human Bird 4/3.
 *
 * "Affinity for Birds. When Bartz and Boko enters, each other Bird you control deals damage equal
 *  to its power to target creature an opponent controls."
 *
 * Two Storm Crows (1/2 Birds) deal 1 each = 2 damage to a 4/4 victim, which therefore survives.
 * That survival is the proof that Bartz itself is excluded ("each *other* Bird"): were Bartz's own
 * power of 4 counted, the victim would take 6 and die.
 */
class BartzAndBokoScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun markedDamage(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("each other Bird deals its own power to the targeted creature; Bartz excludes itself") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Two other Birds you control (power 1 each) and a 4/4 victim.
        driver.putCreatureOnBattlefield(active, "Storm Crow")
        driver.putCreatureOnBattlefield(active, "Storm Crow")
        val victim = driver.putCreatureOnBattlefield(opponent, "Air Elemental") // 4/4

        val bartz = driver.putCardInHand(active, "Bartz and Boko")
        driver.giveMana(active, Color.GREEN, 2)
        driver.giveColorlessMana(active, 3)

        driver.castSpell(active, bartz).isSuccess shouldBe true
        driver.bothPass() // Bartz resolves and enters; the ETB trigger goes on the stack

        val decision = driver.pendingDecision
        (decision is ChooseTargetsDecision) shouldBe true
        driver.submitTargetSelection(active, listOf(victim))
        driver.bothPass() // resolve the ETB trigger

        // 1 + 1 = 2 from the two Storm Crows; Bartz's own 4 power is NOT added.
        markedDamage(driver, victim) shouldBe 2
        // The 4/4 survives 2 damage — confirming Bartz didn't add its own 4 (which would total 6).
        driver.getCardName(victim) shouldBe "Air Elemental"
    }
})
