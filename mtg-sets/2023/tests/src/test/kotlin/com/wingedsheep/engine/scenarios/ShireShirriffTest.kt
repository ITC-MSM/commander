package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Shire Shirriff — "When this creature enters, you may sacrifice a token. When you do, exile
 * target creature an opponent controls until this creature leaves the battlefield."
 *
 * Regression: the optional reflexive trigger ("you may [action]. When you do, [reflexive]") gated
 * the may-decision on [ReflexiveTriggerEffectExecutor.isActionFeasible], which only understood a
 * few action shapes and fell through to "feasible" for a [SacrificeEffect]. So with no token to
 * sacrifice the player was still asked, and answering yes performed an empty sacrifice yet still
 * fired the exile payoff. The fix teaches isActionFeasible that a sacrifice needs enough controlled
 * matching permanents.
 */
class ShireShirriffTest : ScenarioTestBase() {

    init {
        test("no token to sacrifice: the may-decision is skipped and nothing is exiled") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Shire Shirriff")
                .withCardOnBattlefield(1, "Plains")
                .withCardOnBattlefield(1, "Plains")
                .withCardOnBattlefield(2, "Easterling Vanguard")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Mountain")
                .build()

            game.castSpell(1, "Shire Shirriff").error shouldBe null
            game.resolveStack()

            // Alice controls no token, so the sacrifice is infeasible — no prompt is presented...
            game.state.pendingDecision shouldBe null
            // ...and the opponent's creature is NOT exiled.
            game.isOnBattlefield("Easterling Vanguard") shouldBe true
            game.isOnBattlefield("Shire Shirriff") shouldBe true
        }

        test("with a token: sacrificing it exiles the opponent's creature") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Shire Shirriff")
                .withCardOnBattlefield(1, "Plains")
                .withCardOnBattlefield(1, "Plains")
                .withCardOnBattlefield(1, "Food", isToken = true)
                .withCardOnBattlefield(2, "Easterling Vanguard")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Mountain")
                .build()

            game.castSpell(1, "Shire Shirriff").error shouldBe null
            game.resolveStack()

            // The sacrifice IS feasible now, so we're asked.
            game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            game.answerYesNo(true)

            // Sacrificing the token fires the reflexive exile, which targets the opponent's creature.
            val vanguard = game.findPermanent("Easterling Vanguard")
            vanguard.shouldNotBeNull()
            game.selectTargets(listOf(vanguard))
            game.resolveStack()

            game.isOnBattlefield("Easterling Vanguard") shouldBe false  // exiled
            game.isOnBattlefield("Food") shouldBe false                 // sacrificed
        }
    }
}
