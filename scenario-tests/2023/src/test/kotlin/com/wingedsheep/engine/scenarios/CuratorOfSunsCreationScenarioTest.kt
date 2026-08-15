package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.DiscoveredEvent
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CuratorOfSunsCreation
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Scenario coverage for Curator of Sun's Creation — "Whenever you discover, discover again for the
 * same value. This ability triggers only once each turn." — which exercises the new
 * `WheneverYouDiscover` trigger (CR 701.57 / 701.57b) and the `TRIGGER_DISCOVER_VALUE` payload.
 *
 * Each test pins one clause:
 *  - the trigger fires after a discover completes and re-discovers (the DiscoveredEvent lands from a
 *    completed resolution, not the paused may-cast batch);
 *  - the re-discover uses the *same value* N (not 0/default) — discriminated by a card above the
 *    threshold that the second discover must skip;
 *  - `oncePerTurn` caps the otherwise-infinite self-chain at exactly two discovers.
 */
class CuratorOfSunsCreationScenarioTest : FunSpec({

    // MV 1 nonland, castable with no targets — a discover hit for any N >= 1.
    val pebble = card("Pebble") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(1) }
    }
    // MV 3 nonland — above a "Discover 2" threshold, so a discover for 2 skips it.
    val trinket = card("Trinket") {
        manaCost = "{3}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(1) }
    }
    val discoverFour = card("Discover Four") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.Discover(4) }
    }
    val discoverTwo = card("Discover Two") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.Discover(2) }
    }

    fun driver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(pebble)
        driver.registerCard(trinket)
        driver.registerCard(discoverFour)
        driver.registerCard(discoverTwo)
        driver.registerCard(CuratorOfSunsCreation)
        return driver
    }

    fun GameTestDriver.castDiscover(me: EntityId, name: String) {
        val spell = putCardInHand(me, name)
        giveColorlessMana(me, 1)
        submit(CastSpell(playerId = me, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)).isSuccess shouldBe true
        bothPass()
    }

    // Drive every discover that pauses for the cast/hand decision with [castForFree], and resolve
    // the Curator trigger (and any free-cast spells) between discovers. Stops when nothing is left
    // on the stack or paused.
    fun GameTestDriver.settleDiscovers(me: EntityId, castForFree: Boolean) {
        var guard = 0
        while (guard++ < 12) {
            val decision = pendingDecision
            when {
                decision is YesNoDecision -> submitYesNo(me, choice = castForFree)
                stackSize > 0 -> bothPass()
                else -> return
            }
        }
    }

    test("Curator re-discovers after you discover, and caps the self-chain at exactly two discovers") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Curator of Sun's Creation")

        // Top → bottom: Pebble (first discover), Pebble (Curator's re-discover), then Forests.
        driver.putCardOnTopOfLibrary(me, "Pebble")
        driver.putCardOnTopOfLibrary(me, "Pebble")

        val before = driver.events.size
        driver.castDiscover(me, "Discover Four")
        driver.settleDiscovers(me, castForFree = false)

        // Both discovers put their Pebble into hand.
        driver.getHand(me).map { driver.getCardName(it) }.count { it == "Pebble" } shouldBe 2
        // Exactly two discovers happened — the original plus one Curator re-discover; Curator's own
        // discover did NOT re-trigger it (once each turn), so the chain terminates.
        driver.events.drop(before).filterIsInstance<DiscoveredEvent>().size shouldBe 2
        driver.isPaused shouldBe false
    }

    test("the re-discover reuses the triggering discover's value N, not a default of 0") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Curator of Sun's Creation")

        // Top → bottom: Pebble (first discover for 2), Trinket (MV 3 > 2, Curator's discover skips
        // it), Pebble (MV 1 <= 2, Curator's discovered card).
        driver.putCardOnTopOfLibrary(me, "Pebble")
        driver.putCardOnTopOfLibrary(me, "Trinket")
        driver.putCardOnTopOfLibrary(me, "Pebble")

        driver.castDiscover(me, "Discover Two")
        driver.settleDiscovers(me, castForFree = false)

        val handNames = driver.getHand(me).map { driver.getCardName(it) }
        // Curator discovered for exactly 2: it skipped the MV-3 Trinket and found the second Pebble.
        // A threshold of 0 (value not threaded) would have whiffed and kept only one Pebble; a larger
        // threshold would have stopped on Trinket.
        handNames.count { it == "Pebble" } shouldBe 2
        handNames shouldNotContain "Trinket"
    }

    test("Curator re-triggers even when the discover whiffs — no nonland <= N found (CR 701.57b)") {
        // CR 701.57b: a player has "discovered" once the process completes, "even if some or all of
        // those actions were impossible." A discover into an all-land library exiles the whole
        // library, finds no nonland <= N, puts nothing into hand — yet the DiscoveredEvent still
        // fires (bundled as the tail of the discover's follow-up), so Curator re-discovers and
        // once-per-turn caps the chain at two.
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Curator of Sun's Creation")

        // Library is all Forests (lands) — every discover whiffs: it exhausts the library without
        // exiling a nonland <= N, bottom-randomizes, and discovers nothing.
        val handBefore = driver.getHand(me).size
        val before = driver.events.size
        driver.castDiscover(me, "Discover Four")
        driver.settleDiscovers(me, castForFree = false)

        // Exactly two discovers — the original whiff plus one Curator re-discover; once-per-turn
        // caps the chain even though neither discover found a card.
        driver.events.drop(before).filterIsInstance<DiscoveredEvent>().size shouldBe 2
        // Nothing was discovered, so nothing entered hand (the cast Discover Four resolved to the
        // graveyard, leaving the hand exactly as it was before the cast).
        driver.getHand(me).size shouldBe handBefore
        driver.isPaused shouldBe false
    }

    test("Curator still fires when the discovered card is cast for free (not just put into hand)") {
        // Regression: the cast-for-free branch returns triggersAlreadyProcessed = true (to protect
        // the discovered card's own SpellCastEvent from a re-scan), which used to also suppress the
        // DiscoveredEvent the discover tail emits — so Curator never triggered on "cast for free."
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Curator of Sun's Creation")

        // Top → bottom: Pebble (first discover, cast free), Pebble (Curator's re-discover, cast free).
        driver.putCardOnTopOfLibrary(me, "Pebble")
        driver.putCardOnTopOfLibrary(me, "Pebble")

        val before = driver.events.size
        val lifeBefore = driver.getLifeTotal(me)
        driver.castDiscover(me, "Discover Four")
        driver.settleDiscovers(me, castForFree = true)

        // Curator fired: exactly two discovers (original + one Curator), once-per-turn caps the chain.
        driver.events.drop(before).filterIsInstance<DiscoveredEvent>().size shouldBe 2
        // Both discovered Pebbles were cast for free and resolved (GainLife 1 each) → +2 life.
        driver.getLifeTotal(me) shouldBe lifeBefore + 2
        driver.isPaused shouldBe false
    }
})
