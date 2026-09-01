package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Induce Paranoia — {2}{U}{U} instant.
 *
 *   Counter target spell. If {B} was spent to cast this spell, that spell's controller mills X
 *   cards, where X is the spell's mana value.
 *
 * The rider is the interesting half. Both of its references — "that spell's controller" and "the
 * spell's mana value" — are read *after* the counter has already put the spell in its owner's
 * graveyard, so a naive implementation either mills the wrong player (falling back to "you") or
 * mills zero (failing to find the mana value). The two tests pin the count and the victim
 * separately from the payment gate.
 */
class InduceParanoiaScenarioTest : FunSpec({

    test("black mana spent: the countered spell's controller mills its mana value") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val active = driver.activePlayer!!
        val counterer = driver.getOpponent(active)

        // Grizzly Bears is MV 2, so the rider should mill exactly two.
        val bears = driver.putCardInHand(active, "Grizzly Bears")
        driver.giveMana(active, Color.GREEN, 2)
        driver.castSpell(active, bears, emptyList())
        val spellOnStack = driver.getTopOfStack()!!
        // The caster keeps priority after putting a spell on the stack; hand it to the counterer.
        driver.passPriority(active)

        val induce = driver.putCardInHand(counterer, "Induce Paranoia")
        // {2}{U}{U} paid as two blue plus two black: black covers the generic, so black was spent.
        driver.giveMana(counterer, Color.BLUE, 2)
        driver.giveMana(counterer, Color.BLACK, 2)

        val graveyardBefore = driver.getGraveyard(active).size
        driver.castSpellWithTargets(counterer, induce, listOf(ChosenTarget.Spell(spellOnStack)))
            .error shouldBe null
        driver.bothPass()

        withClue("Grizzly Bears was countered") {
            driver.getGraveyardCardNames(active).contains("Grizzly Bears") shouldBe true
        }
        withClue("Two milled cards land on top of the countered spell itself") {
            driver.getGraveyard(active).size shouldBe graveyardBefore + 3
        }
        withClue("The mill hits the countered spell's controller, not Induce Paranoia's") {
            driver.getGraveyard(counterer).size shouldBe 1 // Induce Paranoia itself
        }
    }

    test("no black mana spent: the spell is countered and nobody mills") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val active = driver.activePlayer!!
        val counterer = driver.getOpponent(active)

        val bears = driver.putCardInHand(active, "Grizzly Bears")
        driver.giveMana(active, Color.GREEN, 2)
        driver.castSpell(active, bears, emptyList())
        val spellOnStack = driver.getTopOfStack()!!
        // The caster keeps priority after putting a spell on the stack; hand it to the counterer.
        driver.passPriority(active)

        val induce = driver.putCardInHand(counterer, "Induce Paranoia")
        driver.giveMana(counterer, Color.BLUE, 4)

        val graveyardBefore = driver.getGraveyard(active).size
        driver.castSpellWithTargets(counterer, induce, listOf(ChosenTarget.Spell(spellOnStack)))
            .error shouldBe null
        driver.bothPass()

        withClue("Only the countered spell reaches the graveyard") {
            driver.getGraveyard(active).size shouldBe graveyardBefore + 1
        }
    }
})
