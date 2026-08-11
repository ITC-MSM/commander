package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Proves the built-in AI actually chooses a value for `{X}` when it casts an X-cost *spell*.
 *
 * There is no engine-side choose-X pause on the cast path the way there is for an activated
 * ability: a `CastSpell` submitted with no `xValue` is bound to X=0 as it goes on the stack
 * (CR 601.2b), so before [XCostSelection] every X spell in a deck was dead weight. This test pins
 * the fix end to end; [XCostSelectionTest] covers which X values are offered and why.
 *
 * Goldvein Hydra ({X}{G}, "enters with X +1/+1 counters") is the card because the X choice shows up
 * directly in the thing the evaluator measures — at X=0 it is a 0/0 that dies immediately, and each
 * extra X is a bigger body. That keeps the test about whether an X was chosen at all, rather than
 * about how the evaluator prices a subtler payoff.
 */
class XCostSpellAiTest : ScenarioTestBase() {

    // The AI runs its own registry/simulator, built the same way ScenarioTestBase builds its own.
    private val aiRegistry: CardRegistry = CardRegistry().apply {
        register(TestCards.all)
        register(PredefinedTokens.allTokens)
    }

    private fun gameWithHydra(forests: Int) = scenario()
        .withPlayers()
        .withCardInHand(1, "Goldvein Hydra")
        .withLandsOnBattlefield(1, "Forest", forests)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("AI casts an X-cost creature spell for a real X, not the default of 0") {
            val game = gameWithHydra(forests = 7)

            val ai = AIPlayer.create(aiRegistry, game.player1Id)
            val cast = ai.chooseAction(game.state).shouldBeInstanceOf<CastSpell>()

            game.state.getEntity(cast.cardId)?.get<CardComponent>()?.name shouldBe "Goldvein Hydra"
            cast.xValue.shouldNotBeNull()
            cast.xValue!! shouldBeGreaterThanOrEqual 1
        }

        test("the chosen X never exceeds what the AI can pay") {
            // Two Forests pay {X}{G} at X=1 and no more.
            val game = gameWithHydra(forests = 2)

            val ai = AIPlayer.create(aiRegistry, game.player1Id)
            val cast = ai.chooseAction(game.state).shouldBeInstanceOf<CastSpell>()

            cast.xValue shouldBe 1
        }
    }
}
