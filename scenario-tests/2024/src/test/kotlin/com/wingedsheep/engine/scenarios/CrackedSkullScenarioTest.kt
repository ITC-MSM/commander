package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cracked Skull (DSK #88) — {2}{B} Enchantment — Aura. Enchant creature.
 *
 * "When this Aura enters, look at target player's hand. You may choose a nonland card from it.
 *  That player discards that card."
 * "When enchanted creature is dealt damage, destroy it."
 *
 * Both abilities compose existing primitives (LookAtTargetHand → Gather → SelectFromCollection →
 * MoveCollection discard; and a `takesDamage(binding = ATTACHED)` → Destroy EnchantedCreature).
 */
class CrackedSkullScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun inGraveyard(driver: GameTestDriver, owner: EntityId, id: EntityId): Boolean =
        driver.state.getZone(ZoneKey(owner, Zone.GRAVEYARD)).contains(id)

    /**
     * Drive the Cracked Skull ETB decisions: target the given player's hand, then optionally pick a
     * card to discard (or none). Then pass priority until the stack is empty.
     */
    fun resolveEtb(driver: GameTestDriver, controller: EntityId, targetPlayer: EntityId, discard: EntityId?) {
        var guard = 0
        while (guard++ < 12) {
            when (val decision = driver.state.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(controller, listOf(targetPlayer))
                is SelectCardsDecision ->
                    driver.submitCardSelection(controller, if (discard != null) listOf(discard) else emptyList())
                null -> {
                    if (driver.state.stack.isEmpty()) return
                    driver.bothPass()
                }
                else -> error("Unexpected decision: $decision")
            }
        }
    }

    test("enchanted creature is destroyed when it is dealt damage") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        // Opponent's 1/4 Horned Turtle — 2 damage from Shock is NOT lethal on its own.
        val turtle = driver.putCreatureOnBattlefield(p2, "Horned Turtle")

        // Cast Cracked Skull ({2}{B}) on the turtle, then resolve the ETB (opponent has an empty hand).
        driver.giveMana(p1, Color.BLACK, 3)
        val skull = driver.putCardInHand(p1, "Cracked Skull")
        driver.castSpellWithTargets(p1, skull, listOf(ChosenTarget.Permanent(turtle))).error shouldBe null
        driver.bothPass()
        resolveEtb(driver, p1, p2, discard = null)

        // Shock the turtle for 2 (non-lethal vs toughness 4) — Cracked Skull destroys it.
        driver.giveMana(p1, Color.RED, 1)
        val shock = driver.putCardInHand(p1, "Shock")
        driver.castSpellWithTargets(p1, shock, listOf(ChosenTarget.Permanent(turtle))).error shouldBe null
        var guard = 0
        while (guard++ < 8 && !(driver.state.stack.isEmpty() && driver.state.pendingDecision == null)) {
            driver.bothPass()
        }

        inGraveyard(driver, p2, turtle) shouldBe true
    }

    test("ETB makes target player discard a chosen nonland card") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        val ownCreature = driver.putCreatureOnBattlefield(p1, "Horned Turtle")
        driver.giveMana(p1, Color.BLACK, 3)

        // Give the opponent exactly one nonland card to discard.
        val victimCard = driver.putCardInHand(p2, "Horned Turtle")

        val skull = driver.putCardInHand(p1, "Cracked Skull")
        driver.castSpellWithTargets(p1, skull, listOf(ChosenTarget.Permanent(ownCreature))).error shouldBe null
        driver.bothPass()
        resolveEtb(driver, p1, p2, discard = victimCard)

        inGraveyard(driver, p2, victimCard) shouldBe true
    }
})
