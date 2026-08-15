package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseReplacementDecision
import com.wingedsheep.engine.core.ReplacementChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.mechanics.mana.IntrinsicManaAbilities
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.CryptAngel
import com.wingedsheep.mtg.sets.definitions.inv.cards.CrystalSpray
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Crystal Spray (INV #50) — Invasion engine gap #17: text-changing (color word / basic land type).
 *
 * "{2}{U} Instant — Change the text of target spell or permanent by replacing all instances of one
 * color word with another or one basic land type with another until end of turn. Draw a card."
 *
 * Because a basic-land-type change flows through the projected type line, the land's mana
 * production follows automatically (IntrinsicManaAbilities derives color from projected subtype);
 * a color-word change rewrites protection-from-color. The change expires at end of turn.
 */
class CrystalSprayTest : FunSpec({

    val projector = StateProjector()

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CrystalSpray, CryptAngel))
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Forest" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    fun GameTestDriver.intrinsicColors(landId: EntityId): List<Color> =
        IntrinsicManaAbilities.forEntity(state, projector.project(state), landId)
            .mapNotNull { (it.effect as? AddManaEffect)?.color }

    fun GameTestDriver.answerReplacement(playerId: EntityId, from: String, to: String) {
        val decision = pendingDecision as ChooseReplacementDecision
        submitDecision(
            playerId,
            ReplacementChosenResponse(decision.id, decision.fromOptions.indexOf(from), decision.toOptions.indexOf(to))
        )
    }

    test("replacing Forest with Island makes the land tap for blue, and draws a card") {
        val (driver, player) = newGame()
        val handBefore = driver.getHandSize(player)

        val forest = driver.putLandOnBattlefield(player, "Forest")
        driver.intrinsicColors(forest) shouldBe listOf(Color.GREEN)

        val spray = driver.putCardInHand(player, "Crystal Spray")
        driver.giveMana(player, Color.BLUE, 3) // {2}{U}
        driver.castSpell(player, spray, listOf(forest))
        driver.bothPass() // resolve Crystal Spray -> FROM choice

        driver.answerReplacement(player, "Forest", "Island")

        val projected = projector.project(driver.state)
        projected.getSubtypes(forest) shouldBe setOf("Island")
        driver.intrinsicColors(forest) shouldBe listOf(Color.BLUE)

        // Cast Crystal Spray (left hand) + drew one card => one net card more than before the spray.
        driver.getHandSize(player) shouldBe handBefore + 1
    }

    test("the basic-land-type change expires at end of turn") {
        val (driver, player) = newGame()
        val forest = driver.putLandOnBattlefield(player, "Forest")
        val spray = driver.putCardInHand(player, "Crystal Spray")
        driver.giveMana(player, Color.BLUE, 3)
        driver.castSpell(player, spray, listOf(forest))
        driver.bothPass()
        driver.answerReplacement(player, "Forest", "Island")
        projector.project(driver.state).getSubtypes(forest) shouldBe setOf("Island")

        // Advance into the opponent's turn; this turn's cleanup strips the end-of-turn change.
        driver.passPriorityUntil(Step.UPKEEP)
        projector.project(driver.state).getSubtypes(forest) shouldBe setOf("Forest")
        driver.intrinsicColors(forest) shouldBe listOf(Color.GREEN)
    }

    test("replacing a color word rewrites protection from that color") {
        val (driver, player) = newGame()
        val angel = driver.putCreatureOnBattlefield(player, "Crypt Angel")
        // The cheat helper skips GameInitializer's protection extraction, so attach the
        // "protection from white" component directly.
        driver.addComponent(
            angel,
            com.wingedsheep.engine.state.components.identity.ProtectionComponent(colors = setOf(Color.WHITE))
        )
        projector.project(driver.state).getKeywords(angel) shouldContain "PROTECTION_FROM_WHITE"

        val spray = driver.putCardInHand(player, "Crystal Spray")
        driver.giveMana(player, Color.BLUE, 3)
        driver.castSpell(player, spray, listOf(angel))
        driver.bothPass()

        driver.answerReplacement(player, "White", "Red")

        val keywords = projector.project(driver.state).getKeywords(angel)
        keywords shouldContain "PROTECTION_FROM_RED"
        keywords shouldNotContain "PROTECTION_FROM_WHITE"
    }
})
