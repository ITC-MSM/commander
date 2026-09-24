package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ruric Thar, Magecrusher (FRA #265) — {5}{G}{G} Legendary Creature — Ogre Warrior 7/7.
 *
 *   This spell can't be countered.
 *   Reach, vigilance, trample
 *   Ruric Thar has hexproof as long as they haven't dealt combat damage yet.
 *
 * The hexproof is `Not(SourceHasDealtCombatDamage)` — `HasDealtDamage(combatOnly = true)` over the
 * per-permanent damage marker's combat stamp. Combat damage to a player or to a creature ends it;
 * noncombat damage does not.
 */
class RuricTharMagecrusherScenarioTest : ScenarioTestBase() {

    private val ruric = "Ruric Thar, Magecrusher"

    init {
        test("has reach, vigilance, trample, and hexproof before dealing combat damage") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, ruric)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val id = game.findPermanent(ruric)!!
            val projected = game.state.projectedState
            projected.hasKeyword(id, Keyword.REACH) shouldBe true
            projected.hasKeyword(id, Keyword.VIGILANCE) shouldBe true
            projected.hasKeyword(id, Keyword.TRAMPLE) shouldBe true
            projected.hasKeyword(id, Keyword.HEXPROOF) shouldBe true
        }

        test("loses hexproof after dealing combat damage to a player") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, ruric)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val id = game.findPermanent(ruric)!!
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf(ruric to 2)).error shouldBe null
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            game.getLifeTotal(2) shouldBe 13
            game.state.projectedState.hasKeyword(id, Keyword.HEXPROOF) shouldBe false
        }

        test("combat damage to a blocking creature also ends the hexproof") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, ruric)
                .withCardOnBattlefield(2, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val id = game.findPermanent(ruric)!!
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf(ruric to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareBlockers(mapOf("Hill Giant" to listOf(ruric))).error shouldBe null
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            withClue("trample carried the excess to the player") {
                game.getLifeTotal(2) shouldBe 16
            }
            game.isInGraveyard(2, "Hill Giant") shouldBe true
            game.state.projectedState.hasKeyword(id, Keyword.HEXPROOF) shouldBe false
        }

        test("noncombat damage does not end the hexproof") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, ruric)
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInHand(1, "Rabid Bite")
                .withLandsOnBattlefield(1, "Forest", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val id = game.findPermanent(ruric)!!
            val giant = game.findPermanent("Hill Giant")!!
            val bite = game.findCardsInHand(1, "Rabid Bite").single()
            game.execute(
                CastSpell(
                    game.player1Id,
                    bite,
                    listOf(ChosenTarget.Permanent(id), ChosenTarget.Permanent(giant))
                )
            ).error shouldBe null
            game.resolveStack()

            game.isInGraveyard(2, "Hill Giant") shouldBe true
            game.state.projectedState.hasKeyword(id, Keyword.HEXPROOF) shouldBe true
        }
    }
}
