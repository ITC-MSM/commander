package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BonehoardDracosaur
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Bonehoard Dracosaur (LCI #134).
 *
 * Bonehoard Dracosaur {3}{R}{R}
 * Creature — Dinosaur Dragon 5/5
 * Flying, first strike
 * At the beginning of your upkeep, exile the top two cards of your library.
 * You may play them this turn. If you exiled a land card this way, create a 3/1 red
 * Dinosaur creature token. If you exiled a nonland card this way, create a Treasure token.
 *
 * Coverage:
 * 1. Land on top + nonland second → both tokens created; both cards exiled and playable.
 * 2. Two lands on top → only a Dinosaur token (no Treasure).
 * 3. Two nonlands on top → only a Treasure token (no Dinosaur token).
 * 4. Only one card in library → one card exiled, correct token fires; no crash.
 */
class BonehoardDracosaurScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCard(BonehoardDracosaur)
        // Treasure is a registry-backed predefined token; register it so the "nonland exiled"
        // branch (Effects.CreateTreasure) can mint one.
        driver.registerCards(TestCards.all + PredefinedTokens.Treasure)
        return driver
    }

    /**
     * Advance the game from the controller's turn-1 main phase to *their own* next upkeep,
     * where Bonehoard Dracosaur's trigger fires, then resolve everything off the stack.
     *
     * `passPriorityUntil(Step.UPKEEP)` first stops at the opponent's upkeep (the next upkeep
     * in turn order), so we step past the opponent's turn and reach the controller's own
     * upkeep before resolving the triggered ability.
     */
    fun GameTestDriver.advanceToMyUpkeepAndResolve(me: EntityId) {
        passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (activePlayer != me) {
            // We stopped at the opponent's upkeep; step through their turn to our next upkeep.
            passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
            passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        activePlayer shouldBe me
        // Resolve the upkeep trigger (and anything else it puts on the stack).
        var guard = 0
        while (!isPaused && state.stack.isNotEmpty() && guard < 50) {
            bothPass()
            guard++
        }
    }

    /**
     * Scenario 1: land on top, nonland second.
     *
     * Expected: both top-two cards exiled and playable this turn; a 3/1 Dinosaur token
     * and a Treasure token are both created (+2 permanents on battlefield).
     */
    test("upkeep with land + nonland on top creates both Dinosaur token and Treasure token") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 30),
            startingLife = 20
        )

        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Bonehoard Dracosaur")

        // Put library top: Mountain (land) as card 1, Savannah Lions (nonland) as card 2.
        driver.putCardOnTopOfLibrary(p1, "Savannah Lions") // goes on top first (becomes #2)
        driver.putCardOnTopOfLibrary(p1, "Mountain")       // goes on top second (becomes #1)

        val permanentsBefore = driver.getPermanents(p1).size // Dracosaur only

        driver.advanceToMyUpkeepAndResolve(p1)

        // Both cards are now in exile.
        val exiledNames = driver.getExileCardNames(p1)
        exiledNames.size shouldBe 2
        exiledNames.contains("Mountain") shouldBe true
        exiledNames.contains("Savannah Lions") shouldBe true

        // May-play permission covers the exiled cards this turn.
        val exiledIds = driver.getExile(p1)
        val hasPermission = driver.state.mayPlayPermissions.any { perm ->
            exiledIds.any { it in perm.cardIds }
        }
        hasPermission shouldBe true

        // +1 Dinosaur token (land exiled) + +1 Treasure token (nonland exiled) = +2.
        driver.getPermanents(p1).size shouldBe permanentsBefore + 2
        // The Dinosaur token is a creature, raising creature count by 1 (Dracosaur + Dino).
        driver.getCreatures(p1).size shouldBe permanentsBefore + 1
    }

    /**
     * Scenario 2: two lands on top.
     *
     * Expected: Dinosaur token created; no Treasure (no nonland was exiled).
     */
    test("upkeep with two lands creates only a Dinosaur token, no Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 30),
            startingLife = 20
        )

        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Bonehoard Dracosaur")

        driver.putCardOnTopOfLibrary(p1, "Mountain")
        driver.putCardOnTopOfLibrary(p1, "Mountain")

        val permanentsBefore = driver.getPermanents(p1).size // Dracosaur only
        val creaturesBefore = driver.getCreatures(p1).size    // Dracosaur only

        driver.advanceToMyUpkeepAndResolve(p1)

        driver.getExileCardNames(p1).size shouldBe 2

        // Exactly one new permanent: the Dinosaur creature token.
        driver.getPermanents(p1).size shouldBe permanentsBefore + 1
        driver.getCreatures(p1).size shouldBe creaturesBefore + 1
    }

    /**
     * Scenario 3: two nonlands on top.
     *
     * Expected: Treasure token created; no Dinosaur token (no land was exiled).
     */
    test("upkeep with two nonlands creates only a Treasure token, no Dinosaur") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 30),
            startingLife = 20
        )

        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Bonehoard Dracosaur")

        driver.putCardOnTopOfLibrary(p1, "Savannah Lions")
        driver.putCardOnTopOfLibrary(p1, "Savannah Lions")

        val permanentsBefore = driver.getPermanents(p1).size // Dracosaur only
        val creaturesBefore = driver.getCreatures(p1).size    // Dracosaur only

        driver.advanceToMyUpkeepAndResolve(p1)

        driver.getExileCardNames(p1).size shouldBe 2

        // Exactly one new permanent: the Treasure token (an artifact, not a creature).
        driver.getPermanents(p1).size shouldBe permanentsBefore + 1
        // No new creatures: Dinosaur token was NOT created.
        driver.getCreatures(p1).size shouldBe creaturesBefore
    }

    /**
     * Scenario 4: only one card left in library.
     *
     * Expected: one card exiled (not two), no crash; correct token fires based on its type.
     * The lone library card is a Mountain (land) → Dinosaur token, no Treasure.
     */
    test("upkeep with one-card library exiles that card and creates the correct token") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 30),
            startingLife = 20
        )

        val p1 = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Bonehoard Dracosaur")

        // Trim library to exactly one Mountain.
        val libraryKey = ZoneKey(p1, Zone.LIBRARY)
        val oneCard = driver.state.getZone(libraryKey).take(1)
        driver.replaceState(
            driver.state.copy(
                zones = driver.state.zones + (libraryKey to oneCard)
            )
        )
        driver.state.getZone(libraryKey).size shouldBe 1

        val permanentsBefore = driver.getPermanents(p1).size
        val creaturesBefore = driver.getCreatures(p1).size

        driver.advanceToMyUpkeepAndResolve(p1)

        // Only one card exiled (library had one card).
        driver.getExileCardNames(p1).size shouldBe 1

        // Library is now empty.
        driver.state.getZone(libraryKey).size shouldBe 0

        // The lone exiled card was a Mountain (land) → Dinosaur token (+1 creature), no Treasure.
        driver.getPermanents(p1).size shouldBe permanentsBefore + 1
        driver.getCreatures(p1).size shouldBe creaturesBefore + 1
    }
})
