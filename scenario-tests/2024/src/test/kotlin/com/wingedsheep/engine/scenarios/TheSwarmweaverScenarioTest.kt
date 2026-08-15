package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.TheSwarmweaver
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Swarmweaver (DSK #236) — {2}{B}{G} Legendary Artifact Creature — Scarecrow 2/3.
 *
 * "When The Swarmweaver enters, create two 1/1 black and green Insect creature tokens with flying.
 * Delirium — As long as there are four or more card types among cards in your graveyard, Insects and
 * Spiders you control get +1/+1 and have deathtouch."
 *
 * Exercises: the ETB token creation (two 1/1 flying Insects), and the Delirium-gated conditional
 * lord (Insects/Spiders are 1/1 without four card types in the graveyard, and 2/2 with deathtouch
 * once Delirium is satisfied).
 */
class TheSwarmweaverScenarioTest : FunSpec({

    val projector = StateProjector()

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TheSwarmweaver))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    // Four distinct card types in the graveyard so Delirium is satisfied.
    fun GameTestDriver.fillGraveyardForDelirium(player: EntityId) {
        putCardInGraveyard(player, "Centaur Courser")   // creature
        putCardInGraveyard(player, "Lightning Bolt")     // instant
        putCardInGraveyard(player, "Careful Study")      // sorcery
        putCardInGraveyard(player, "Test Enchantment")   // enchantment
    }

    fun GameTestDriver.insectTokens(player: EntityId): List<EntityId> =
        getCreatures(player).filter { id ->
            projector.project(state).getSubtypes(id).contains("Insect")
        }

    // Cast The Swarmweaver from hand and resolve it (and its ETB trigger) onto the battlefield.
    fun GameTestDriver.castAndResolveSwarmweaver(player: EntityId) {
        giveMana(player, Color.BLACK, 3)
        giveMana(player, Color.GREEN, 1)
        val card = putCardInHand(player, "The Swarmweaver")
        castSpell(player, card)
        var guard = 0
        while ((state.stack.isNotEmpty() || pendingDecision != null) && guard++ < 20) bothPass()
    }

    test("ETB creates two 1/1 black-green flying Insect tokens") {
        val driver = newDriver()
        val me = driver.player1

        driver.castAndResolveSwarmweaver(me)

        val tokens = driver.insectTokens(me)
        tokens.size shouldBe 2
        val projected = projector.project(driver.state)
        tokens.forEach { id ->
            projected.getPower(id) shouldBe 1
            projected.getToughness(id) shouldBe 1
            projected.hasKeyword(id, Keyword.FLYING) shouldBe true
        }
    }

    test("without Delirium, Insect tokens stay 1/1 with no deathtouch") {
        val driver = newDriver()
        val me = driver.player1

        driver.castAndResolveSwarmweaver(me)
        // Only one card type in graveyard — Delirium not satisfied.
        driver.putCardInGraveyard(me, "Lightning Bolt")

        val token = driver.insectTokens(me).first()
        val projected = projector.project(driver.state)
        projected.getPower(token) shouldBe 1
        projected.getToughness(token) shouldBe 1
        projected.hasKeyword(token, Keyword.DEATHTOUCH) shouldBe false
    }

    test("with Delirium, Insects and Spiders get +1/+1 and deathtouch") {
        val driver = newDriver()
        val me = driver.player1

        driver.castAndResolveSwarmweaver(me)
        driver.fillGraveyardForDelirium(me)

        val token = driver.insectTokens(me).first()
        val projected = projector.project(driver.state)
        projected.getPower(token) shouldBe 2
        projected.getToughness(token) shouldBe 2
        projected.hasKeyword(token, Keyword.DEATHTOUCH) shouldBe true
    }
})
