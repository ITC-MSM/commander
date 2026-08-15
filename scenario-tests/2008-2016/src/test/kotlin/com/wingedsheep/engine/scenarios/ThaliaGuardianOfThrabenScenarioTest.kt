package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Thalia, Guardian of Thraben (DKA #24) — {1}{W} Legendary Creature — Human Soldier, 2/1.
 *
 * "First strike
 *  Noncreature spells cost {1} more to cast."
 *
 * The canonical CardDefinition lives in DKA (Thalia's earliest real-expansion printing); VOW gets
 * only a Printing(...) row. The tax is a symmetric [ModifySpellCost] over every caster's noncreature
 * spells — identical in shape to Glowrider (LGN) — so these tests mirror that card's coverage:
 *   - the tax applies to the controller's own noncreature spells,
 *   - creature spells are untouched,
 *   - the opponent's noncreature spells are taxed too (symmetry),
 *   - and Thalia carries first strike.
 */
class ThaliaGuardianOfThrabenScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("Thalia has first strike") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val thalia = driver.putCreatureOnBattlefield(player, "Thalia, Guardian of Thraben")
        driver.state.projectedState.hasKeyword(thalia, Keyword.FIRST_STRIKE) shouldBe true
    }

    test("noncreature spell cannot be cast without paying the {1} tax") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Thalia, Guardian of Thraben")

        // Test Enchantment normally costs {1}{W}; with Thalia it costs {2}{W}. Give only {1}{W}
        // worth of mana (two white) — a targetless enchantment, so the failure is the tax alone.
        val enchantment = driver.putCardInHand(player, "Test Enchantment")
        driver.giveMana(player, Color.WHITE, 2)

        driver.submit(
            CastSpell(playerId = player, cardId = enchantment, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe false
    }

    test("noncreature spell can be cast with enough mana to pay the tax") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Thalia, Guardian of Thraben")

        // {2}{W} (three white) covers Test Enchantment ({1}{W}) plus Thalia's {1} tax.
        val enchantment = driver.putCardInHand(player, "Test Enchantment")
        driver.giveMana(player, Color.WHITE, 3)

        driver.submit(
            CastSpell(playerId = player, cardId = enchantment, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
    }

    test("Thalia does not tax creature spells") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Plains" to 20), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Thalia, Guardian of Thraben")

        // Grizzly Bears ({1}{G}) is a creature spell — untaxed, so exactly {1}{G} suffices.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.WHITE, 1)

        driver.submit(
            CastSpell(playerId = player, cardId = bears, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
    }

    test("Thalia taxes the opponent's noncreature spells too") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.player1
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Thalia on the active player's side; the opponent's noncreature spell is still taxed
        // (the effect is symmetric — it applies to every caster).
        driver.putCreatureOnBattlefield(player, "Thalia, Guardian of Thraben")

        val enchantment = driver.putCardInHand(opponent, "Test Enchantment")
        driver.giveMana(opponent, Color.WHITE, 2) // only {1}{W} — not enough with the {1} tax
        driver.passPriority(player) // hand priority to the opponent

        driver.submit(
            CastSpell(playerId = opponent, cardId = enchantment, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe false
    }
})
