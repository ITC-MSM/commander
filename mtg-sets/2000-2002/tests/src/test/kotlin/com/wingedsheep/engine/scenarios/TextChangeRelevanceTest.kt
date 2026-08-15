package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseReplacementDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ons.cards.ArtificialEvolution
import com.wingedsheep.mtg.sets.definitions.ons.cards.WirewoodSavage
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

/**
 * The text-change "from" picker highlights words that will actually take effect. A creature type
 * is relevant if it's a subtype OR appears in the card's rules text — Artificial Evolution changes
 * "all instances of one creature type", and the trigger resolver rewrites types named in abilities.
 *
 * Regression: Wirewood Savage is an Elf whose trigger references Beast; both must be flagged
 * "On <card>" (previously only the subtype Elf was, so Beast looked inert in the UI).
 */
class TextChangeRelevanceTest : FunSpec({

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ArtificialEvolution, WirewoodSavage))
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Forest" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    test("Artificial Evolution marks both the subtype (Elf) and a text-referenced type (Beast)") {
        val (driver, player) = newGame()
        val savage = driver.putCreatureOnBattlefield(player, "Wirewood Savage")
        val ae = driver.putCardInHand(player, "Artificial Evolution")
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, ae, listOf(savage))
        driver.bothPass() // resolve -> single from/to replacement choice

        val decision = driver.pendingDecision as ChooseReplacementDecision

        fun isOnCard(type: String): Boolean {
            val idx = decision.fromOptions.indexOf(type)
            return decision.fromMetadata.getOrNull(idx)?.description?.startsWith("On Wirewood Savage") == true
        }
        isOnCard("Elf") shouldBe true
        isOnCard("Beast") shouldBe true
        // On-card types sort to the front of the FROM list.
        decision.fromOptions.take(2) shouldContainAll listOf("Elf", "Beast")
    }
})
