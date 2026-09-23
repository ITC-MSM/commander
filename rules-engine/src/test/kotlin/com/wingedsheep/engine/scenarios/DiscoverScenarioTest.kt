package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.engine.core.Outcome

/**
 * Engine coverage for Discover N (CR 701.57), exercised through inline test spells so each
 * clause of the rule is pinned:
 *  - exile from the top until a **nonland** card with mana value **≤ N** (the discovered card);
 *  - a two-option prompt — cast for free **or** put into hand;
 *  - the remaining exiled cards go to the bottom (they leave exile);
 *  - a whiff (no nonland ≤ N) offers no decision and no follow-up;
 *  - `thenEffect` runs only when a card was discovered, reading the discovered card's mana value
 *    (Hit the Mother Lode's Treasure payoff).
 */
class DiscoverScenarioTest : FunSpec({

    // MV 1 nonland, castable with no targets — a legal discover hit for any N >= 1.
    val pebble = card("Pebble") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(1) }
    }
    // MV 7 nonland — above the "Discover 4" threshold, so it is skipped (exiled, then bottomed).
    val boulder = card("Boulder") {
        manaCost = "{7}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(1) }
    }
    // MV 11 nonland — above even "Discover 10", so a library of these decks out with the final
    // card's mana value > N, i.e. no discovered card at all (CR 701.57c).
    val colossus = card("Colossus") {
        manaCost = "{11}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(1) }
    }
    val discoverFour = card("Discover Four") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.Discover(4) }
    }
    // Hit the Mother Lode shape: Discover 10, then tapped Treasures equal to (10 - discovered MV).
    val motherLode = card("Test Mother Lode") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.Discover(
                amount = 10,
                storeDiscoveredAs = "discovered",
                thenEffect = Effects.CreateTreasure(
                    count = DynamicAmount.IfPositive(
                        DynamicAmount.Subtract(
                            DynamicAmount.Fixed(10),
                            DynamicAmount.StoredCardManaValue("discovered")
                        )
                    ),
                    tapped = true
                )
            )
        }
    }

    fun driverWith(vararg extra: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens)
        driver.registerCard(pebble)
        driver.registerCard(boulder)
        driver.registerCard(discoverFour)
        driver.registerCard(motherLode)
        extra.forEach { driver.registerCard(it) }
        return driver
    }

    fun GameTestDriver.castDiscover(me: com.wingedsheep.sdk.model.EntityId, name: String) {
        val spell = putCardInHand(me, name)
        giveColorlessMana(me, 1)
        submit(CastSpell(playerId = me, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)).outcome shouldBe Outcome.Done
        bothPass()
    }

    test("discovered card cast for free ends up on the stack; the skipped land is bottomed, exile is empty") {
        val driver = driverWith()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Top → bottom: Forest (land, skipped), then Pebble (MV 1 ≤ 4, the discovered card).
        driver.putCardOnTopOfLibrary(me, "Pebble")
        driver.putCardOnTopOfLibrary(me, "Forest")

        driver.castDiscover(me, "Discover Four")
        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        driver.submitYesNo(me, choice = true) // cast for free

        driver.getStackSpellNames() shouldContain "Pebble"
        driver.getExile(me).size shouldBe 0
    }

    test("discovered card put into hand when the player declines the free cast") {
        val driver = driverWith()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCardOnTopOfLibrary(me, "Pebble")
        driver.putCardOnTopOfLibrary(me, "Forest")

        driver.castDiscover(me, "Discover Four")
        driver.submitYesNo(me, choice = false) // put into hand

        driver.getHand(me).map { driver.getCardName(it) } shouldContain "Pebble"
        driver.getExile(me).size shouldBe 0
    }

    test("a nonland above the threshold is skipped (exiled then bottomed), not discovered") {
        val driver = driverWith()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Top → bottom: Boulder (MV 7 > 4, skipped), Forest (land, skipped), Pebble (MV 1, discovered).
        driver.putCardOnTopOfLibrary(me, "Pebble")
        driver.putCardOnTopOfLibrary(me, "Forest")
        driver.putCardOnTopOfLibrary(me, "Boulder")

        driver.castDiscover(me, "Discover Four")
        driver.isPaused shouldBe true

        driver.submitYesNo(me, choice = false)

        driver.getHand(me).map { driver.getCardName(it) } shouldContain "Pebble"
        // Boulder was skipped and bottomed — not kept, not in exile.
        driver.getHand(me).map { driver.getCardName(it) } shouldNotContain "Boulder"
        driver.getExile(me).size shouldBe 0
    }

    test("whiff — no nonland with mana value <= N — offers no decision and no card is kept") {
        val driver = driverWith()
        // Library is all lands, so Discover 4 finds no nonland ≤ 4 and bottoms everything.
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val handBefore = driver.getHandSize(me)

        driver.castDiscover(me, "Discover Four")

        driver.isPaused shouldBe false
        driver.getExile(me).size shouldBe 0
        driver.getHandSize(me) shouldBe handBefore
    }

    test("Mother Lode shape — thenEffect makes tapped Treasures equal to 10 minus the discovered MV") {
        val driver = driverWith()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCardOnTopOfLibrary(me, "Pebble") // MV 1 → 10 - 1 = 9 Treasures
        driver.putCardOnTopOfLibrary(me, "Forest")

        driver.castDiscover(me, "Test Mother Lode")
        driver.submitYesNo(me, choice = false) // keep Pebble in hand; Treasures still made

        val treasures = driver.state.getBattlefield()
            .mapNotNull { driver.state.getEntity(it)?.get<CardComponent>() }
            .count { it.name == "Treasure" }
        treasures shouldBe 9
        driver.getHand(me).map { driver.getCardName(it) } shouldContain "Pebble"
    }

    test("Mother Lode decks out on lands — final card (a land, MV 0) is still the discovered card, so 10 Treasures (CR 701.57c)") {
        val driver = driverWith()
        // All-land library: Discover 10 exiles the whole library without a castable nonland ≤ 10,
        // so there is no cast/hand decision. But the final card exiled is a Forest (mana value 0),
        // which is the discovered card per CR 701.57c — so the follow-up still makes 10 - 0 Treasures.
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.castDiscover(me, "Test Mother Lode")

        driver.isPaused shouldBe false // no castable stopping card, so no decision
        val treasures = driver.state.getBattlefield()
            .mapNotNull { driver.state.getEntity(it)?.get<CardComponent>() }
            .count { it.name == "Treasure" }
        treasures shouldBe 10
    }

    test("Mother Lode true whiff — final card is a nonland with MV > N, so no discovered card and no Treasures") {
        val driver = driverWith(colossus)
        // Library is all MV-11 nonlands: Discover 10 exiles everything, and the final card exiled
        // has mana value 11 > 10, so nothing is discovered (CR 701.57c) and the follow-up is skipped.
        driver.initMirrorMatch(deck = Deck.of("Colossus" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.castDiscover(me, "Test Mother Lode")

        driver.isPaused shouldBe false
        val treasures = driver.state.getBattlefield()
            .mapNotNull { driver.state.getEntity(it)?.get<CardComponent>() }
            .count { it.name == "Treasure" }
        treasures shouldBe 0
    }
})
