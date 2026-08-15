package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.KainTraitorousDragoon
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Kain, Traitorous Dragoon (FIN 105) — {2}{B} Legendary Creature — Human Knight, 2/4.
 *
 * "Jump — During your turn, Kain has flying.
 *  Whenever Kain deals combat damage to a player, that player gains control of Kain. If they do,
 *  you draw that many cards, create that many tapped Treasure tokens, then lose that much life."
 *
 * Proves the composition:
 *  - Jump grants flying only while it's the controller's turn (conditional static ability).
 *  - On combat damage, control of Kain moves to the damaged player, and — gated on the control
 *    actually moving — the ability's original controller draws / mints tapped Treasures / loses
 *    life equal to the combat damage dealt.
 */
class KainTraitorousDragoonScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all +
                com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens +
                KainTraitorousDragoon,
        )
        d.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20, skipMulligans = true)
        return d
    }

    fun treasureCount(d: GameTestDriver, player: EntityId, tappedOnly: Boolean): Int =
        d.getPermanents(player).count { id ->
            d.state.getEntity(id)?.get<CardComponent>()?.name == "Treasure" &&
                (!tappedOnly || d.isTapped(id))
        }

    test("Jump — Kain has flying during its controller's turn, but not on the opponent's turn") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        val kain = d.putCreatureOnBattlefield(p1, "Kain, Traitorous Dragoon")

        // p1 is the active player, so it's the controller's turn: Kain has flying.
        d.state.projectedState.hasKeyword(kain, Keyword.FLYING) shouldBe true

        // Advance to the opponent's turn.
        var guard = 0
        while (d.activePlayer != p2 && guard++ < 4) {
            d.passPriorityUntil(Step.END)
            d.bothPass()
        }
        d.activePlayer shouldBe p2

        // It is no longer Kain's controller's turn, so Jump no longer grants flying.
        d.state.projectedState.hasKeyword(kain, Keyword.FLYING) shouldBe false
    }

    test("combat damage hands Kain to the damaged player and pays off the rider") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        val kain = d.putCreatureOnBattlefield(p1, "Kain, Traitorous Dragoon")
        d.removeSummoningSickness(kain)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(kain), p2)
        d.bothPass()
        d.declareNoBlockers(p2)

        val handBefore = d.getHandSize(p1)

        // Pass priority so combat damage is dealt and the mandatory trigger resolves. The control
        // change is a Layer.CONTROL floating effect, so it shows up in projected state.
        var guard = 0
        while (d.state.projectedState.getController(kain) != p2 && guard++ < 12) {
            d.bothPass()
        }

        // Kain (2/4) dealt 2 combat damage → "that player" (p2) now controls Kain.
        d.state.projectedState.getController(kain) shouldBe p2
        // "If they do" — control moved, so the riders fire, all scaling off the 2 damage dealt:
        d.getHandSize(p1) shouldBe handBefore + 2          // you draw that many cards
        treasureCount(d, p1, tappedOnly = true) shouldBe 2 // create that many tapped Treasure tokens
        d.getLifeTotal(p1) shouldBe 18                     // then lose that much life
    }
})
