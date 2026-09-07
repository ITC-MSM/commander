package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.GaddockTeeg
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Gaddock Teeg — "Noncreature spells with mana value 4 or greater can't be cast. Noncreature spells
 * with {X} in their mana costs can't be cast."
 *
 * Two [com.wingedsheep.sdk.scripting.PlayersCantCastSpells] statics scoped to `Player.Each`. What
 * the test is for is the *union* of the two clauses and the fact that the lock binds Teeg's own
 * controller: both are easy to get silently wrong (one clause dropped, or `EachOpponent` copied in
 * from the family's more common wording), and neither shows up in a snapshot diff.
 *
 * Probes are instants so that baseline castability isn't masked by sorcery timing — a
 * sorcery-speed spell wouldn't enumerate on the opponent's turn regardless of Teeg.
 */
class GaddockTeegScenarioTest : FunSpec({

    val bigInstant = card("Teeg Test Deluge") { manaCost = "{3}{U}"; typeLine = "Instant"; oracleText = "" }
    val smallInstant = card("Teeg Test Ripple") { manaCost = "{2}{U}"; typeLine = "Instant"; oracleText = "" }
    val xInstant = card("Teeg Test Surge") { manaCost = "{X}{U}"; typeLine = "Instant"; oracleText = "" }
    val bigCreature = card("Teeg Test Colossus") {
        manaCost = "{5}{G}"; typeLine = "Creature — Giant"; power = 6; toughness = 6
    }

    fun driver(startingPlayer: Int = 0) = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(GaddockTeeg, bigInstant, smallInstant, xInstant, bigCreature))
        initMirrorMatch(Deck.of("Forest" to 40), startingLife = 20, startingPlayer = startingPlayer)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.castActionsFor(playerId: EntityId, cardId: EntityId): List<CastSpell> =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, playerId)
            .mapNotNull { it.action as? CastSpell }
            .filter { it.cardId == cardId }

    test("a noncreature spell with mana value 4 can't be cast by the opponent") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Gaddock Teeg")
        val spell = d.putCardInHand(d.player2, "Teeg Test Deluge")
        d.giveMana(d.player2, Color.BLUE, 4)

        d.castActionsFor(d.player2, spell) shouldHaveSize 0
    }

    test("the lock binds Teeg's own controller too — the wording is \"can't be cast\"") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Gaddock Teeg")
        val spell = d.putCardInHand(d.player1, "Teeg Test Deluge")
        d.giveMana(d.player1, Color.BLUE, 4)

        d.castActionsFor(d.player1, spell) shouldHaveSize 0
    }

    test("a cheaper noncreature spell is unaffected") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Gaddock Teeg")
        val spell = d.putCardInHand(d.player2, "Teeg Test Ripple")
        d.giveMana(d.player2, Color.BLUE, 3)

        d.castActionsFor(d.player2, spell).isNotEmpty() shouldBe true
    }

    test("a big creature spell is unaffected — the restriction is noncreature only") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Gaddock Teeg")
        val spell = d.putCardInHand(d.player1, "Teeg Test Colossus")
        d.giveMana(d.player1, Color.GREEN, 6)

        d.castActionsFor(d.player1, spell).isNotEmpty() shouldBe true
    }

    test("an {X} noncreature spell is blocked by the second clause, which the first would miss") {
        val d = driver()
        // Sanity: {X}{U} has mana value 1 in hand (CR 202.3b), so clause one alone lets it through.
        val without = driver()
        val freeSpell = without.putCardInHand(without.player2, "Teeg Test Surge")
        without.giveMana(without.player2, Color.BLUE, 3)
        without.castActionsFor(without.player2, freeSpell).isNotEmpty() shouldBe true

        d.putCreatureOnBattlefield(d.player1, "Gaddock Teeg")
        val spell = d.putCardInHand(d.player2, "Teeg Test Surge")
        d.giveMana(d.player2, Color.BLUE, 3)

        d.castActionsFor(d.player2, spell) shouldHaveSize 0
    }

    test("the lock lifts when Teeg leaves the battlefield") {
        val d = driver()
        val teeg = d.putCreatureOnBattlefield(d.player1, "Gaddock Teeg")
        val spell = d.putCardInHand(d.player2, "Teeg Test Deluge")
        d.giveMana(d.player2, Color.BLUE, 4)
        d.castActionsFor(d.player2, spell) shouldHaveSize 0

        d.moveToGraveyard(teeg)
        d.castActionsFor(d.player2, spell).isNotEmpty() shouldBe true
    }
})
