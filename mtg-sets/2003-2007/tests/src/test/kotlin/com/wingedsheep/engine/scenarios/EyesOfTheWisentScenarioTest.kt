package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.EyesOfTheWisent
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Eyes of the Wisent (LRW #210) — {1}{G} Kindred Enchantment — Elemental
 *
 *   Whenever an opponent casts a blue spell during your turn, you may create a 4/4 green
 *   Elemental creature token.
 *
 * Three axes have to hold at once and each can silently swallow the trigger: the *caster* must be
 * an opponent, the *spell* must be blue, and the *turn* must be yours. The last is a CR 603.2
 * trigger restriction rather than an intervening "if", so it is only ever read at cast time.
 */
class EyesOfTheWisentScenarioTest : FunSpec({

    val blueBlink = CardDefinition.instant(
        name = "Test Blue Instant",
        manaCost = ManaCost.parse("{U}"),
        oracleText = "Do nothing."
    )
    val redBlink = CardDefinition.instant(
        name = "Test Red Instant",
        manaCost = ManaCost.parse("{R}"),
        oracleText = "Do nothing."
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + EyesOfTheWisent + blueBlink + redBlink)
        return driver
    }

    test("an opponent's blue spell on your turn makes a 4/4 Elemental") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(p1, "Eyes of the Wisent")

        val spell = driver.putCardInHand(p2, "Test Blue Instant")
        driver.giveMana(p2, Color.BLUE, 1)
        driver.passPriority(p1)
        driver.castSpell(p2, spell)

        // The trigger is put on the stack above the spell, so it resolves first.
        driver.bothPass()
        driver.submitYesNo(p1, true)

        val creatures = driver.getCreatures(p1)
        creatures.size shouldBe 1
        driver.getCardName(creatures.single()) shouldBe "Elemental Token"
    }

    test("declining the may makes no token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(p1, "Eyes of the Wisent")

        val spell = driver.putCardInHand(p2, "Test Blue Instant")
        driver.giveMana(p2, Color.BLUE, 1)
        driver.passPriority(p1)
        driver.castSpell(p2, spell)

        driver.bothPass()
        driver.submitYesNo(p1, false)

        driver.getCreatures(p1).size shouldBe 0
    }

    test("an opponent's non-blue spell does not trigger it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(p1, "Eyes of the Wisent")

        val spell = driver.putCardInHand(p2, "Test Red Instant")
        driver.giveMana(p2, Color.RED, 1)
        driver.passPriority(p1)
        driver.castSpell(p2, spell)

        withClue("only the red instant is on the stack — the colour filter held") {
            driver.stackSize shouldBe 1
        }
    }

    test("your own blue spell does not trigger it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(p1, "Eyes of the Wisent")

        val spell = driver.putCardInHand(p1, "Test Blue Instant")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.castSpell(p1, spell)

        withClue("the trigger reads 'an opponent casts', so the controller's own spell is inert") {
            driver.stackSize shouldBe 1
        }
    }
})
