package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsDrawnEvent
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.LibraryShuffledEvent
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Tests for the Omen mechanic (Tarkir: Dragonstorm).
 *
 * An Omen card is a permanent (here a creature) whose [com.wingedsheep.sdk.model.CardLayout.OMEN]
 * `cardFaces[0]` is an instant/sorcery spell. Casting the Omen face (via [CastSpell.faceIndex] = 0)
 * is identical to casting an Adventure, but on resolution the card is **shuffled into its owner's
 * library** instead of being exiled with a cast-from-exile permission, and instead of going to the
 * graveyard like a normal spell.
 *
 * Modeled on Dirgur Island Dragon // Skimming Strike — the Omen draws a card and shuffles itself
 * back in. Here the Omen face is reduced to a plain "Draw a card" so the resolution behavior is
 * isolated from target selection.
 *
 * ## Covered scenarios
 * - Casting the Omen resolves the spell effect, then shuffles the card into the library.
 * - The card lands in the library (not graveyard, not exile).
 * - A [LibraryShuffledEvent] is emitted.
 * - The permanent (creature) face still casts normally to the battlefield.
 */
class OmenMechanicTest : FunSpec({

    // Inline Omen test card: {5}{U} Dragon 4/4 with Omen "Skimming Strike" — {1}{U}, Instant — Omen,
    // "Draw a card."
    val islandDragon = card("Island Dragon") {
        manaCost = "{5}{U}"
        typeLine = "Creature — Dragon"
        power = 4
        toughness = 4
        keywords(Keyword.FLYING)
        omen("Skimming Strike") {
            manaCost = "{1}{U}"
            typeLine = "Instant — Omen"
            oracleText = "Draw a card. (Then shuffle this card into its owner's library.)"
            spell {
                effect = Effects.DrawCards(1)
            }
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(islandDragon)
        return driver
    }

    test("Casting the Omen face shuffles the card into its owner's library on resolution") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 40, "Forest" to 20),
            startingLife = 20
        )
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val omen = driver.putCardInHand(player, "Island Dragon")
        driver.giveMana(player, Color.BLUE, 2) // {1}{U}

        // Cast the Omen face (faceIndex = 0), not the creature.
        val cast = driver.submit(
            CastSpell(
                playerId = player,
                cardId = omen,
                faceIndex = 0,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.outcome shouldBe Outcome.Done

        // Resolve the spell.
        driver.bothPass()
        driver.isPaused shouldBe false

        // The card shuffled into the library — not graveyard, not exile.
        driver.state.getZone(ZoneKey(player, Zone.LIBRARY)) shouldContain omen
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)) shouldNotContain omen
        driver.state.getZone(ZoneKey(player, Zone.EXILE)) shouldNotContain omen
        driver.state.getZone(ZoneKey(player, Zone.STACK)) shouldNotContain omen

        // The library was shuffled and the Omen's "Draw a card" resolved.
        driver.events.filterIsInstance<LibraryShuffledEvent>()
            .any { it.playerId == player } shouldBe true
        driver.events.filterIsInstance<CardsDrawnEvent>()
            .any { it.playerId == player } shouldBe true
    }

    test("The permanent face of an Omen card casts normally to the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 40, "Forest" to 20),
            startingLife = 20
        )
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dragon = driver.putCardInHand(player, "Island Dragon")
        driver.giveMana(player, Color.BLUE, 6) // {5}{U}

        // Cast the creature face (no faceIndex).
        val cast = driver.submit(
            CastSpell(
                playerId = player,
                cardId = dragon,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.outcome shouldBe Outcome.Done

        driver.bothPass()
        driver.isPaused shouldBe false

        // The Dragon resolved onto the battlefield — not shuffled away.
        driver.getPermanents(player) shouldContain dragon
        driver.state.getZone(ZoneKey(player, Zone.LIBRARY)) shouldNotContain dragon
    }
})
