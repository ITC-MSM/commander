package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Boros Fury-Shield — {2}{W} instant.
 *
 *   Prevent all combat damage that would be dealt by target attacking or blocking creature this
 *   turn. If {R} was spent to cast this spell, Boros Fury-Shield deals damage to that creature's
 *   controller equal to the creature's power.
 *
 * Two independent things can fail silently here: a shield wired in the wrong direction (preventing
 * damage dealt *to* the creature rather than *by* it), and a rider that reads the wrong power or
 * points the damage at the wrong player. The tests separate the payment gate from the shield so a
 * regression in either is attributable.
 */
class BorosFuryShieldScenarioTest : FunSpec({

    /** Sets up an opposing 2/2 attacking the shield's caster, stopped in the declare-attackers step. */
    fun attackingBears(driver: GameTestDriver): Triple<EntityId, EntityId, EntityId> {
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val defender = driver.activePlayer!!
        val attacker = driver.getOpponent(defender)
        val bears = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        driver.removeSummoningSickness(bears)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (driver.activePlayer != attacker) {
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        }
        driver.declareAttackers(attacker, listOf(bears), defender)
        // The attacking player holds priority after attackers are declared; hand it to the
        // defender so the shield can be cast in the declare-attackers step.
        driver.passPriority(attacker)
        return Triple(defender, attacker, bears)
    }

    test("the shield stops the attacker's combat damage") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        val (defender, attacker, bears) = attackingBears(driver)

        val shield = driver.putCardInHand(defender, "Boros Fury-Shield")
        driver.giveMana(defender, Color.WHITE, 3)
        driver.castSpellWithTargets(defender, shield, listOf(ChosenTarget.Permanent(bears)))
            .error shouldBe null
        driver.bothPass()

        driver.passPriorityUntil(Step.END)

        withClue("The 2/2's combat damage was prevented") {
            driver.getLifeTotal(defender) shouldBe 20
        }
        withClue("No red mana was spent, so the rider did nothing") {
            driver.getLifeTotal(attacker) shouldBe 20
        }
    }

    test("red mana spent: the attacker's controller takes damage equal to its power") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        val (defender, attacker, bears) = attackingBears(driver)

        val shield = driver.putCardInHand(defender, "Boros Fury-Shield")
        // {2}{W} paid as one white plus two red: red covers the generic, so red was spent.
        driver.giveMana(defender, Color.WHITE, 1)
        driver.giveMana(defender, Color.RED, 2)
        driver.castSpellWithTargets(defender, shield, listOf(ChosenTarget.Permanent(bears)))
            .error shouldBe null
        driver.bothPass()

        withClue("Grizzly Bears is a 2/2, and the damage goes to its controller") {
            driver.getLifeTotal(attacker) shouldBe 18
        }

        driver.passPriorityUntil(Step.END)
        withClue("The shield still stopped the combat damage") {
            driver.getLifeTotal(defender) shouldBe 20
        }
    }
})
