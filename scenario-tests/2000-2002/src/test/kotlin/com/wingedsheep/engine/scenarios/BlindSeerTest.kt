package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.BlindSeer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Blind Seer (INV #47) — Invasion engine gap #11: recoloring a spell on the stack.
 *
 * "{1}{U}: Target spell or permanent becomes the color of your choice until end of turn."
 *
 * Verifies the projector now reads the recolored entry for both a battlefield permanent and a
 * spell still on the stack (the latter is the gap the engine couldn't express before).
 */
class BlindSeerTest : FunSpec({

    val abilityId = BlindSeer.activatedAbilities.first().id
    val projector = StateProjector()

    fun newGame(): Pair<GameTestDriver, com.wingedsheep.sdk.model.EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BlindSeer))
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    test("recolors a permanent to the chosen color") {
        val (driver, player) = newGame()
        val seer = driver.putCreatureOnBattlefield(player, "Blind Seer")

        // A green creature to recolor.
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        projector.project(driver.state).getColors(bears) shouldBe setOf("GREEN")

        driver.giveMana(player, Color.BLUE, 2) // pays {1}{U}
        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = seer,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bears))
            )
        ).isSuccess shouldBe true

        driver.bothPass() // resolve the ability -> color choice
        driver.pendingDecision.shouldBeInstanceOf<ChooseColorDecision>()
        val decision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(player, ColorChosenResponse(decision.id, Color.RED))

        // Grizzly Bears is now red (only) — the green was replaced.
        projector.project(driver.state).getColors(bears) shouldBe setOf("RED")
    }

    test("recolors a spell on the stack to the chosen color") {
        val (driver, player) = newGame()
        val opponent = driver.getOpponent(player)
        val seer = driver.putCreatureOnBattlefield(player, "Blind Seer")

        // Cast a red instant; it sits on the stack with the caster retaining priority.
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, listOf(opponent))
        driver.state.stack.contains(bolt) shouldBe true
        // A stack spell has no projection entry until something recolors it; color-matching
        // code falls back to the base red CardComponent. After Blind Seer it projects green.
        projector.project(driver.state).getColors(bolt).isEmpty() shouldBe true

        // In response, Blind Seer recolors the spell to green.
        driver.giveMana(player, Color.BLUE, 2)
        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = seer,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Spell(bolt))
            )
        ).isSuccess shouldBe true

        driver.bothPass() // resolve Blind Seer's ability (top of stack) -> color choice
        val decision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(player, ColorChosenResponse(decision.id, Color.GREEN))

        // The bolt is still on the stack and now projects as green, not red.
        driver.state.stack.contains(bolt) shouldBe true
        projector.project(driver.state).getColors(bolt) shouldBe setOf("GREEN")
    }
})
