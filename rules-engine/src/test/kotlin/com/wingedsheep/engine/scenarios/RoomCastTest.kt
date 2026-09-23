package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.wingedsheep.engine.core.Outcome

/**
 * Phase 2 of the Rooms mechanic (CR 709): split-layout cards can be cast face-by-face,
 * and the resulting permanent enters the battlefield with a [RoomComponent] tracking
 * which doors are unlocked. Phase 2 does not yet implement door-unlock special actions
 * or locked-half ability suppression — those land in Phase 3.
 */
class RoomCastTest : FunSpec({

    // Inline split-layout test fixture. We don't yet have a real Room card registered
    // (Phase 5 will add Unholy Annex), so build one with the SDK's Phase 1 split DSL.
    val testRoom = card("Test Hall // Test Vault") {
        layout = CardLayout.SPLIT
        face("Test Hall") {
            manaCost = "{2}{B}"
            typeLine = "Enchantment — Room"
            oracleText = "Test."
        }
        face("Test Vault") {
            manaCost = "{3}{B}{B}"
            typeLine = "Enchantment — Room"
            oracleText = "Test."
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(testRoom)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Swamp" to 20,
                "Grizzly Bears" to 20,
            ),
            skipMulligans = true,
        )
        return driver
    }

    test("legal actions fan out to one CastSpell per face when both halves are affordable") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = driver.putCardInHand(player, testRoom.name)
        // Test Hall costs {2}{B}, Test Vault costs {3}{B}{B}. Five black covers both.
        driver.giveMana(player, Color.BLACK, 5)

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val legalActions = enumerator.enumerate(driver.state, player)

        val roomCastActions = legalActions
            .mapNotNull { it.action as? CastSpell }
            .filter { it.cardId == roomId }
        roomCastActions shouldHaveSize 2
        roomCastActions.map { it.faceIndex }.toSet() shouldBe setOf(0, 1)
    }

    test("legal actions surface only the affordable face when mana is short") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = driver.putCardInHand(player, testRoom.name)
        // Three black covers Test Hall ({2}{B}) but not Test Vault ({3}{B}{B}).
        driver.giveMana(player, Color.BLACK, 3)

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val roomCastActions = enumerator.enumerate(driver.state, player)
            .mapNotNull { it.action as? CastSpell }
            .filter { it.cardId == roomId }

        roomCastActions shouldHaveSize 1
        roomCastActions.single().faceIndex shouldBe 0
    }

    test("casting a face attaches RoomComponent with that face unlocked and the other locked") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = driver.putCardInHand(player, testRoom.name)
        driver.giveMana(player, Color.BLACK, 3)

        // Cast the left face (Test Hall, faceIndex = 0).
        val castResult = driver.submit(CastSpell(player, roomId, faceIndex = 0))
        castResult.outcome shouldBe Outcome.Done

        // Resolve the spell.
        driver.bothPass()

        val entity = driver.state.getEntity(roomId)
        val room = entity?.get<RoomComponent>()
        room shouldNotBe null
        room!!.faces.map { it.name } shouldBe listOf("Test Hall", "Test Vault")
        room.faces.map { it.manaCost.toString() } shouldBe listOf("{2}{B}", "{3}{B}{B}")
        room.unlocked shouldBe setOf(RoomFaceId("Test Hall"))
        room.isFullyUnlocked shouldBe false
        room.lockedFaces.map { it.name } shouldBe listOf("Test Vault")
    }

    test("casting the right face leaves the left face locked") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = driver.putCardInHand(player, testRoom.name)
        driver.giveMana(player, Color.BLACK, 5)

        val castResult = driver.submit(CastSpell(player, roomId, faceIndex = 1))
        castResult.outcome shouldBe Outcome.Done
        driver.bothPass()

        val room = driver.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Test Vault"))
        room.lockedFaces.map { it.name } shouldBe listOf("Test Hall")
    }
})
