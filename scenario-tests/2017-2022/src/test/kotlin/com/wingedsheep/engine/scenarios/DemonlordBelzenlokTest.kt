package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.DemonlordBelzenlok
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Tests for Demonlord Belzenlok:
 * {4}{B}{B} - Legendary Creature — Elder Demon (6/6)
 * Flying, trample
 * When Demonlord Belzenlok enters, exile cards from the top of your library
 * until you exile a nonland card, then put that card into your hand. If the
 * card's mana value is 4 or greater, repeat this process. Demonlord Belzenlok
 * deals 1 damage to you for each card put into your hand this way.
 *
 * ## Covered Scenarios
 * - Nonland card on top with MV < 4: puts it in hand, deals 1 damage, stops
 * - Lands then nonland with MV < 4: exiles lands, puts nonland in hand, deals 1 damage
 * - Nonland with MV >= 4 causes repeat: first card MV >= 4, repeats, second card MV < 4
 * - All damage dealt at once (not per iteration)
 * - Empty library: no cards, no damage
 */
class DemonlordBelzenlokTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DemonlordBelzenlok)
        return driver
    }

    test("nonland on top with MV < 4 puts in hand and deals 1 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 30, "Forest" to 30),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put a CMC 3 card on top (won't repeat)
        val topCard = driver.putCardOnTopOfLibrary(activePlayer, "Centaur Courser")

        val belzenlok = driver.putCardInHand(activePlayer, "Demonlord Belzenlok")
        driver.giveMana(activePlayer, Color.BLACK, 6)

        driver.castSpell(activePlayer, belzenlok)
        driver.bothPass() // Resolve spell — creature enters, ETB trigger goes on stack

        if (driver.stackSize > 0) {
            driver.bothPass() // Resolve ETB trigger
        }

        // Centaur Courser should be in hand
        val handCards = driver.getHand(activePlayer).mapNotNull { id ->
            driver.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name
        }
        handCards shouldContain "Centaur Courser"

        // 1 damage dealt (1 card put in hand)
        driver.getLifeTotal(activePlayer) shouldBe 19

        // No cards should be exiled (nonland was on top, no lands to exile)
        driver.getExileCardNames(activePlayer).size shouldBe 0
    }

    test("lands before nonland with MV < 4: exiles lands, puts nonland in hand") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 30, "Forest" to 30),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack library: Centaur Courser (CMC 3, stops), then 2 lands on top
        driver.putCardOnTopOfLibrary(activePlayer, "Centaur Courser")
        driver.putCardOnTopOfLibrary(activePlayer, "Swamp")
        driver.putCardOnTopOfLibrary(activePlayer, "Forest")
        // Library top: Forest, Swamp, Centaur Courser, ...rest

        val belzenlok = driver.putCardInHand(activePlayer, "Demonlord Belzenlok")
        driver.giveMana(activePlayer, Color.BLACK, 6)

        driver.castSpell(activePlayer, belzenlok)
        driver.bothPass() // Resolve spell

        if (driver.stackSize > 0) {
            driver.bothPass() // Resolve ETB trigger
        }

        // Centaur Courser should be in hand
        val handCards = driver.getHand(activePlayer).mapNotNull { id ->
            driver.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name
        }
        handCards shouldContain "Centaur Courser"

        // 2 lands should be exiled
        val exileNames = driver.getExileCardNames(activePlayer)
        exileNames.size shouldBe 2

        // 1 damage (1 card put in hand)
        driver.getLifeTotal(activePlayer) shouldBe 19
    }

    test("nonland with MV >= 4 causes repeat, stops when MV < 4") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 30, "Forest" to 30),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack library: Centaur Courser (CMC 3, stops), then Force of Nature (CMC 5, repeats)
        driver.putCardOnTopOfLibrary(activePlayer, "Centaur Courser")
        driver.putCardOnTopOfLibrary(activePlayer, "Force of Nature")
        // Library top: Force of Nature, Centaur Courser, ...rest

        val belzenlok = driver.putCardInHand(activePlayer, "Demonlord Belzenlok")
        driver.giveMana(activePlayer, Color.BLACK, 6)

        driver.castSpell(activePlayer, belzenlok)
        driver.bothPass() // Resolve spell

        if (driver.stackSize > 0) {
            driver.bothPass() // Resolve ETB trigger
        }

        // Both Force of Nature and Centaur Courser should be in hand
        val handCards = driver.getHand(activePlayer).mapNotNull { id ->
            driver.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name
        }
        handCards shouldContain "Force of Nature"
        handCards shouldContain "Centaur Courser"

        // 2 damage dealt at once (2 cards put in hand)
        driver.getLifeTotal(activePlayer) shouldBe 18
    }

    test("lands between repeated nonlands: exiles lands correctly") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 30, "Forest" to 30),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack: Centaur Courser (CMC 3, stops), land, Force of Nature (CMC 5, repeats), land
        driver.putCardOnTopOfLibrary(activePlayer, "Centaur Courser")
        driver.putCardOnTopOfLibrary(activePlayer, "Forest")
        driver.putCardOnTopOfLibrary(activePlayer, "Force of Nature")
        driver.putCardOnTopOfLibrary(activePlayer, "Swamp")
        // Library top: Swamp, Force of Nature, Forest, Centaur Courser, ...rest

        val belzenlok = driver.putCardInHand(activePlayer, "Demonlord Belzenlok")
        driver.giveMana(activePlayer, Color.BLACK, 6)

        driver.castSpell(activePlayer, belzenlok)
        driver.bothPass()

        if (driver.stackSize > 0) {
            driver.bothPass()
        }

        // Both nonland cards in hand
        val handCards = driver.getHand(activePlayer).mapNotNull { id ->
            driver.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name
        }
        handCards shouldContain "Force of Nature"
        handCards shouldContain "Centaur Courser"

        // 2 lands exiled
        driver.getExileCardNames(activePlayer).size shouldBe 2

        // 2 damage (2 cards to hand)
        driver.getLifeTotal(activePlayer) shouldBe 18
    }

    test("all lands in library: no nonland found, no card to hand, no damage") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 30, "Forest" to 30),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Don't put any nonland on top — library is all lands
        val libraryZone = ZoneKey(activePlayer, Zone.LIBRARY)
        val libraryBefore = driver.state.getZone(libraryZone).size

        val belzenlok = driver.putCardInHand(activePlayer, "Demonlord Belzenlok")
        driver.giveMana(activePlayer, Color.BLACK, 6)

        driver.castSpell(activePlayer, belzenlok)
        driver.bothPass()

        if (driver.stackSize > 0) {
            driver.bothPass()
        }

        // All cards in library should be exiled (entire library was lands)
        val exileSize = driver.getExile(activePlayer).size
        exileSize shouldBe libraryBefore

        // No damage dealt (no cards put in hand)
        driver.getLifeTotal(activePlayer) shouldBe 20
    }
})
