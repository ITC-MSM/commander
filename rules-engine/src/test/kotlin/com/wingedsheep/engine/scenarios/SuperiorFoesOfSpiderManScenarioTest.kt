package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.spm.cards.SuperiorFoesOfSpiderMan
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Superior Foes of Spider-Man (SPM #96) — {2}{R} 3/3 Human Rogue Villain, Trample.
 *
 *  "Whenever you cast a spell with mana value 4 or greater, you may exile the top card of your
 *   library. If you do, you may play that card until you exile another card with this creature."
 *
 * Exercises the mana-value-gated cast trigger + optional impulse:
 *  - Casting a mana value 4+ spell offers the "may exile" yes/no; saying yes exiles the top card
 *    and grants a play-from-exile permission.
 *  - Casting a cheaper spell (mana value < 4) does not trigger at all.
 */
class SuperiorFoesOfSpiderManScenarioTest : FunSpec({

    // A mana value 4 creature spell to trigger the ability.
    val bigThreat = card("Big Threat") {
        manaCost = "{4}"
        typeLine = "Creature — Golem"
        power = 4
        toughness = 4
    }

    // A cheap (mana value 1) creature spell that must NOT trigger the ability.
    val smallFry = card("Small Fry") {
        manaCost = "{1}"
        typeLine = "Creature — Goblin"
        power = 1
        toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SuperiorFoesOfSpiderMan + bigThreat + smallFry)
        return driver
    }

    test("casting a mana value 4+ spell lets you exile the top card and play it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Superior Foes of Spider-Man")
        driver.putCardOnTopOfLibrary(player, "Forest")

        val big = driver.putCardInHand(player, "Big Threat")
        driver.giveMana(player, Color.RED, 4)
        driver.castSpell(player, big)

        // Advance until the "may exile" yes/no from the resolving trigger appears.
        var guard = 0
        while (driver.pendingDecision !is YesNoDecision && guard++ < 6) {
            driver.bothPass()
        }
        val yesNo = driver.pendingDecision as YesNoDecision
        driver.submitYesNo(yesNo.playerId, true)

        // The exiled top card is now in exile with a play-from-exile permission.
        driver.getExileCardNames(player) shouldBe listOf("Forest")
        val exiledCard = driver.getExile(player).single()
        driver.state.mayPlayPermissions.any { exiledCard in it.cardIds } shouldBe true
    }

    test("casting a spell with mana value less than 4 does not trigger the ability") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Superior Foes of Spider-Man")
        driver.putCardOnTopOfLibrary(player, "Forest")

        val small = driver.putCardInHand(player, "Small Fry")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, small)

        // No trigger: no "may exile" question is ever raised.
        (driver.pendingDecision is YesNoDecision) shouldBe false
        driver.bothPass() // resolve the small creature spell

        // Nothing was exiled and no play-from-exile permission was granted.
        driver.getExile(player).size shouldBe 0
        driver.state.mayPlayPermissions.size shouldBe 0
    }
})
