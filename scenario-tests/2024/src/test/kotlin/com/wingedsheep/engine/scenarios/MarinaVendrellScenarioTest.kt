package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Marina Vendrell (DSK) — the enters-the-battlefield reveal-and-sort.
 *
 * Oracle: "When Marina Vendrell enters, reveal the top seven cards of your library. Put all
 * enchantment cards from among them into your hand and the rest on the bottom of your library in a
 * random order."
 *
 * Exercises the new [Patterns.Library.revealTopPutAllMatchingToHand] recipe: every enchantment among
 * the top seven goes to hand (no player choice), the rest leave the top of the library. The {T}
 * lock/unlock-door ability reuses the already-tested [Effects.LockOrUnlockDoor] and is not re-covered
 * here.
 */
class MarinaVendrellScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply { registerCards(TestCards.all) }

    fun GameTestDriver.handNames(playerId: EntityId): List<String> =
        getHand(playerId).mapNotNull { state.getEntity(it)?.get<CardComponent>()?.name }

    fun GameTestDriver.libraryNames(playerId: EntityId): List<String> =
        state.getZone(com.wingedsheep.engine.state.ZoneKey(playerId, Zone.LIBRARY))
            .mapNotNull { state.getEntity(it)?.get<CardComponent>()?.name }

    test("ETB: all enchantments among the top seven go to hand, the rest leave the top") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack a known top seven: two enchantments + five non-enchantments.
        listOf(
            "Test Enchantment", "Test Enchantment",
            "Lightning Bolt", "Centaur Courser", "Black Creature", "Artifact Creature", "Lightning Bolt",
        ).forEach { d.putCardOnTopOfLibrary(active, it) }

        val handEnchantmentsBefore = d.handNames(active).count { it == "Test Enchantment" }

        val marina = d.putCardInHand(active, "Marina Vendrell")
        d.giveMana(active, Color.WHITE, 1)
        d.giveMana(active, Color.BLUE, 1)
        d.giveMana(active, Color.BLACK, 1)
        d.giveMana(active, Color.RED, 1)
        d.giveMana(active, Color.GREEN, 1)
        d.castSpell(active, marina, emptyList()).isSuccess shouldBe true
        repeat(10) { if (d.pendingDecision == null) d.bothPass() }

        withClue("Both enchantments among the top seven were put into hand") {
            d.handNames(active).count { it == "Test Enchantment" } shouldBe handEnchantmentsBefore + 2
        }
        withClue("The revealed non-enchantments are no longer on top (moved to the bottom)") {
            val topSeven = d.libraryNames(active).take(7)
            topSeven.none { it in setOf("Lightning Bolt", "Centaur Courser", "Black Creature", "Artifact Creature") } shouldBe true
        }
    }
})
