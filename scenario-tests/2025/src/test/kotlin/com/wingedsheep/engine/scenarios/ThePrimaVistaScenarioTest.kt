package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for The Prima Vista (FIN #64).
 *
 * The Prima Vista {4}{U} Legendary Artifact — Vehicle 5/3
 * Flying
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, The Prima
 * Vista becomes an artifact creature until end of turn.
 * Crew 2.
 *
 * Exercises the [com.wingedsheep.sdk.dsl.Conditions.TriggeringSpellManaSpentAtLeast] intervening-if
 * on the [com.wingedsheep.sdk.dsl.Triggers.YouCastNoncreature] cast trigger: a 4-mana noncreature
 * spell animates the Vehicle into an artifact creature until end of turn; a 1-mana one does not.
 */
class ThePrimaVistaScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("casting a 4-mana noncreature spell makes The Prima Vista an artifact creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prima = driver.putPermanentOnBattlefield(active, "The Prima Vista")

        withClue("the Vehicle starts as a noncreature artifact") {
            driver.state.projectedState.isCreature(prima) shouldBe false
        }

        // Cast Stoke the Flames ({2}{R}{R}, a 4-mana instant) targeting the opponent, paying full mana.
        val stoke = driver.putCardInHand(active, "Stoke the Flames")
        driver.giveMana(active, Color.RED, 2)
        driver.giveColorlessMana(active, 2)
        driver.castSpellWithTargets(
            active,
            stoke,
            listOf(entityIdToChosenTarget(driver.state, opponent)),
        ).isSuccess shouldBe true
        // Resolve The Prima Vista's trigger (on top of the stack), then the spell.
        driver.bothPass()
        driver.bothPass()

        withClue("four mana was spent, so The Prima Vista is now an artifact creature") {
            driver.state.projectedState.isCreature(prima) shouldBe true
        }
        withClue("printed 5/3") {
            driver.state.projectedState.getPower(prima) shouldBe 5
            driver.state.projectedState.getToughness(prima) shouldBe 3
        }
    }

    test("casting a sub-4-mana noncreature spell does not animate The Prima Vista") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prima = driver.putPermanentOnBattlefield(active, "The Prima Vista")

        // Lightning Bolt ({R}) is a 1-mana noncreature spell — below the four-mana threshold.
        val bolt = driver.putCardInHand(active, "Lightning Bolt")
        driver.giveMana(active, Color.RED, 1)
        driver.castSpellWithTargets(
            active,
            bolt,
            listOf(entityIdToChosenTarget(driver.state, opponent)),
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        withClue("fewer than four mana was spent, so the Vehicle stays a noncreature artifact") {
            driver.state.projectedState.isCreature(prima) shouldBe false
        }
    }
})
