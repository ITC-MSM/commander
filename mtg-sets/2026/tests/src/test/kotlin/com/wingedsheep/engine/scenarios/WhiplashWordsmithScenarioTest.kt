package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Whiplash Wordsmith (FRA #164) — has flying and haste only while an opponent has been dealt
 * noncombat damage this turn.
 */
class WhiplashWordsmithScenarioTest : ScenarioTestBase() {
    init {
        fun game() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Whiplash Wordsmith")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardInHand(1, "Shock")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("gains flying and haste once an opponent is dealt noncombat damage, and loses them next turn") {
            val game = game()
            val wordsmith = game.findPermanent("Whiplash Wordsmith").shouldNotBeNull()
            game.state.projectedState.hasKeyword(wordsmith, Keyword.FLYING) shouldBe false
            game.state.projectedState.hasKeyword(wordsmith, Keyword.HASTE) shouldBe false

            game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
            game.resolveStack()

            game.state.projectedState.hasKeyword(wordsmith, Keyword.FLYING) shouldBe true
            game.state.projectedState.hasKeyword(wordsmith, Keyword.HASTE) shouldBe true

            withClue("the record is this turn's only") {
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.projectedState.hasKeyword(wordsmith, Keyword.FLYING) shouldBe false
            }
        }

        test("noncombat damage to a creature doesn't count") {
            val game = game()
            val wordsmith = game.findPermanent("Whiplash Wordsmith").shouldNotBeNull()
            val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

            game.castSpell(1, "Shock", bears).error shouldBe null
            game.resolveStack()

            game.state.projectedState.hasKeyword(wordsmith, Keyword.FLYING) shouldBe false
        }

        test("noncombat damage to yourself doesn't count") {
            val game = game()
            val wordsmith = game.findPermanent("Whiplash Wordsmith").shouldNotBeNull()

            game.castSpellTargetingPlayer(1, "Shock", 1).error shouldBe null
            game.resolveStack()

            game.state.projectedState.hasKeyword(wordsmith, Keyword.HASTE) shouldBe false
        }
    }
}
