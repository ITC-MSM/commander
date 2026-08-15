package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.CanAttackDespiteDefenderThisTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Stalked Researcher (DSK #73) — {1}{U} 3/3 Creature — Human Wizard, Defender.
 *
 * "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 *  this creature can attack this turn as though it didn't have defender."
 *
 * Exercises the enchantment-enters half of the Eerie ability granting the temporary
 * [com.wingedsheep.sdk.scripting.effects.CanAttackDespiteDefenderThisTurnEffect]: with Defender it
 * cannot attack, but once an enchantment you control enters it gains the transient marker and
 * can attack this turn.
 */
class StalkedResearcherScenarioTest : ScenarioTestBase() {

    init {
        context("Stalked Researcher — Eerie grants attack despite defender") {

            test("cannot attack with Defender before any enchantment enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Stalked Researcher", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val result = game.declareAttackers(mapOf("Stalked Researcher" to 2))
                withClue("Defender blocks the attack before Eerie fires") {
                    (result.error != null) shouldBe true
                }
            }

            test("an enchantment you control entering lets it attack this turn despite Defender") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Stalked Researcher", tapped = false, summoningSickness = false)
                    .withCardInHand(1, "Test Enchantment") // {1}{W}
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val researcher = game.findPermanent("Stalked Researcher")!!

                val cast = game.castSpell(1, "Test Enchantment")
                withClue("Casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Eerie fired — Researcher gains the can-attack-despite-defender marker") {
                    game.state.getEntity(researcher)
                        ?.get<CanAttackDespiteDefenderThisTurnComponent>().shouldNotBeNull()
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackers(mapOf("Stalked Researcher" to 2))
                withClue("After Eerie fired the Researcher can attack despite Defender: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("Stalked Researcher is now an attacker") {
                    game.state.getEntity(researcher)?.get<AttackingComponent>().shouldNotBeNull()
                }
            }
        }
    }
}
