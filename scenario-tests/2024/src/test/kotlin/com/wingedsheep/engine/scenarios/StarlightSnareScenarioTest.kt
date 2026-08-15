package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.StarlightSnare
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Starlight Snare (FDN #514) — {2}{U} Enchantment — Aura.
 *
 * "Enchant creature
 *  When this Aura enters, tap enchanted creature.
 *  Enchanted creature doesn't untap during its controller's untap step."
 *
 * Pins both halves: the ETB trigger taps the enchanted creature, and the
 * `DOESNT_UNTAP` grant keeps it tapped through its controller's next untap step —
 * including when the Aura is cast on an *opponent's* creature, which is the way the
 * card is actually played.
 */
class StarlightSnareScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + StarlightSnare)
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Grizzly Bears" to 20))
        return driver
    }

    // Cast Starlight Snare from the active player onto [creature], resolving the Aura
    // and its ETB tap trigger.
    fun enchant(driver: GameTestDriver, caster: EntityId, creature: EntityId) {
        val aura = driver.putCardInHand(caster, "Starlight Snare")
        driver.giveMana(caster, Color.BLUE, 3)
        driver.castSpellWithTargets(caster, aura, listOf(ChosenTarget.Permanent(creature)))
            .isSuccess shouldBe true

        driver.bothPass() // Aura resolves, ETB trigger goes on the stack
        driver.bothPass() // ETB trigger resolves
    }

    // Drive one full turn cycle so [activePlayer] passes through their next untap step.
    fun advanceToNextUntapStep(driver: GameTestDriver, activePlayer: EntityId) {
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.END)
        driver.passPriority(activePlayer)
        driver.passPriority(opponent)
        // Opponent's turn.
        driver.passPriorityUntil(Step.END)
        driver.passPriority(opponent)
        driver.passPriority(activePlayer)
        // Back to the active player's turn — their untap step already happened.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("the enters trigger taps the enchanted creature") {
        val driver = createDriver()
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.isTapped(creature) shouldBe false

        enchant(driver, p1, creature)

        driver.isTapped(creature) shouldBe true
    }

    test("the enchanted creature doesn't untap during its controller's untap step") {
        val driver = createDriver()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // The realistic line: snare an opponent's creature. Its controller is p2, so it is
        // p2's untap step that must fail to untap it.
        val victim = driver.putCreatureOnBattlefield(p2, "Grizzly Bears")
        enchant(driver, p1, victim)
        driver.isTapped(victim) shouldBe true

        advanceToNextUntapStep(driver, p1)

        // p2's untap step came and went; the creature is still tapped.
        driver.isTapped(victim) shouldBe true
    }
})
