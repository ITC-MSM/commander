package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.WalkInClosetForgottenCellar
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Scenario for `Walk-In Closet // Forgotten Cellar` (DSK 205), a split-layout Room (CR 709.5).
 *
 * Forgotten Cellar's unlock trigger grants two durational "this turn" riders — the runtime
 * siblings of Festival of Embers' printed abilities: a granted [MayCastFromGraveyard] (read by the
 * graveyard-cast enumerator/resolver) and a granted [RedirectZoneChange] to exile (read by the
 * zone-change redirect path). Both expire in the cleanup step. These tests exercise the new
 * `GrantReplacementEffect` engine feature and the granted-static graveyard-cast read path.
 */
class WalkInClosetForgottenCellarTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(WalkInClosetForgottenCellar)
        d.initMirrorMatch(
            deck = Deck.of("Forest" to 30, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    /** Pass priority / resolve the stack until a decision surfaces or the stack empties. */
    fun resolveStack(d: GameTestDriver) {
        var guard = 0
        while (d.pendingDecision == null && d.getTopOfStackName() != null && guard++ < 8) {
            d.bothPass()
        }
    }

    /** Cast Forgotten Cellar (face 1) so it enters unlocked and resolves its unlock trigger. */
    fun unlockForgottenCellar(d: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) {
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = d.putCardInHand(player, WalkInClosetForgottenCellar.name)
        d.giveMana(player, Color.GREEN, 5)
        d.submitSuccess(CastSpell(player, roomId, faceIndex = 1))
        // Resolve the room spell (enters unlocked) and then its unlock trigger.
        d.bothPass()
        if (d.pendingDecision == null) d.bothPass()
    }

    test("Forgotten Cellar unlock exiles a creature that would die this turn") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        unlockForgottenCellar(d, p1)
        d.state.grantedReplacementEffects.isNotEmpty().shouldBeTrue()

        // A creature p1 owns and controls that dies should be exiled instead of hitting the graveyard.
        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val boltId = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, boltId, targets = listOf(bears))
        resolveStack(d)

        d.getExile(p1).shouldContain(bears)
        d.getGraveyard(p1).shouldNotContain(bears)
    }

    test("the granted riders expire at end of turn") {
        val d = driver()
        val p1 = d.activePlayer!!

        unlockForgottenCellar(d, p1)
        d.state.grantedReplacementEffects.isNotEmpty().shouldBeTrue()

        // Advance past the end of the turn — the EndOfTurn grants are cleaned up.
        d.passPriorityUntil(Step.END)
        d.bothPass()

        d.state.grantedReplacementEffects.isEmpty().shouldBeTrue()
        d.state.grantedStaticAbilities.none {
            it.ability is com.wingedsheep.sdk.scripting.MayCastFromGraveyard
        }.shouldBeTrue()
    }

    test("Forgotten Cellar unlock lets you cast a spell from your graveyard") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        unlockForgottenCellar(d, p1)

        // A nonland spell already in the graveyard becomes castable this turn.
        val boltId = d.putCardInGraveyard(p1, "Lightning Bolt")
        val p2StartLife = d.state.lifeTotal(p2)
        d.giveMana(p1, Color.RED, 1)
        // Legal to cast from the graveyard (would throw on failure) — Lightning Bolt at p2's face.
        d.castSpell(p1, boltId, targets = listOf(p2))
        resolveStack(d)

        // It resolved (3 damage to p2) and left the graveyard — proving it was cast from there.
        d.state.lifeTotal(p2) shouldBe (p2StartLife - 3)
        d.getGraveyard(p1).shouldNotContain(boltId)
    }
})
