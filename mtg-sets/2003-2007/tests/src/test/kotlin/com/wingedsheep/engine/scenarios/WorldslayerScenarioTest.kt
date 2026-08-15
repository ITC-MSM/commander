package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Worldslayer (MRD #276) — "Whenever equipped creature deals combat damage to a player, destroy all
 * permanents other than this Equipment." Equip {5}.
 *
 * The whole card is one [com.wingedsheep.sdk.dsl.Effects.DestroyAll] over
 * `GameObjectFilter.Permanent.notSourceItself()`, so the two things worth proving are the two halves
 * of that filter: it really does spare Worldslayer (otherwise the card destroys itself and the
 * board-reset-and-re-equip loop it is famous for never happens), and it really does spare *nothing
 * else* — the 2011-09-22 ruling ("The equipped creature is also destroyed") and the lands both fall
 * out of the same filter rather than a carve-out.
 *
 * The third test pins the trigger's recipient: combat damage to a *creature* is not combat damage to
 * a player, so a blocked Worldslayer carrier leaves the board standing.
 */
class WorldslayerScenarioTest : ScenarioTestBase() {

    init {
        context("Worldslayer — combat damage to a player wipes everything but the Equipment") {
            test("every other permanent is destroyed, including the equipped creature and both players' lands") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Worldslayer", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers()
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.passPriority()
                game.resolveStack()

                withClue("`notSourceItself()` is the only exclusion — Worldslayer survives its own wipe") {
                    game.isOnBattlefield("Worldslayer") shouldBe true
                }
                withClue("2011-09-22 ruling: the equipped creature is also destroyed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("'all permanents' means lands too, on both sides") {
                    game.isOnBattlefield("Forest") shouldBe false
                    game.isOnBattlefield("Mountain") shouldBe false
                }
                withClue("the opponent's board goes with it") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                }
            }

            test("combat damage to a blocking creature is not damage to a player — nothing is destroyed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Worldslayer", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardOnBattlefield(2, "Force of Nature", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                // The 5/5 eats the 2/2; the damage the Bears deal goes to a creature, not a player.
                game.declareBlockers(mapOf("Force of Nature" to listOf("Grizzly Bears"))).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.passPriority()
                game.resolveStack()

                withClue("RecipientFilter.AnyPlayer never matched, so no wipe") {
                    game.isOnBattlefield("Force of Nature") shouldBe true
                    game.isOnBattlefield("Forest") shouldBe true
                    game.isOnBattlefield("Worldslayer") shouldBe true
                }
            }
        }
    }
}
