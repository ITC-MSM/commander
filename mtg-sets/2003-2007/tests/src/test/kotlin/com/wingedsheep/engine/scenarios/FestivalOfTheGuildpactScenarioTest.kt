package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Festival of the Guildpact — Ravnica: City of Guilds #17, {X}{W} Instant
 *
 * "Prevent the next X damage that would be dealt to you this turn."
 * "Draw a card."
 *
 * The shield is untargeted and always protects the caster, and its size is the X announced on cast.
 * The failure mode worth pinning is a shield that ignores X and prevents a fixed amount (or
 * nothing): a cantrip that draws correctly hides a prevention that silently did zero. So both a
 * damage total under the shield and one over it are asserted, along with the ruling's ordering —
 * the card is drawn on resolution, not later when damage is actually prevented.
 */
class FestivalOfTheGuildpactScenarioTest : ScenarioTestBase() {

    init {
        context("Festival of the Guildpact") {

            test("X damage is absorbed and the card is drawn on resolution") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Festival of the Guildpact")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Plains") }
                repeat(3) { builder = builder.withCardInLibrary(2, "Mountain") }
                val game = builder.build()

                val handBefore = game.handSize(1)

                // X = 3, exactly enough to eat a Lightning Bolt.
                game.castXSpell(1, "Festival of the Guildpact", 3).error shouldBe null
                game.resolveStack()

                withClue("the cantrip resolves with the spell: -1 for the spell, +1 for the draw") {
                    game.handSize(1) shouldBe handBefore
                }

                // Player 1 still holds priority after their own spell resolved; hand it over
                // so the opponent can respond with the Bolt.
                game.passPriority()
                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.resolveStack()

                withClue("all 3 damage was prevented by the X=3 shield") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }

            test("damage past X still gets through") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Festival of the Guildpact")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Plains") }
                repeat(3) { builder = builder.withCardInLibrary(2, "Mountain") }
                val game = builder.build()

                // X = 1: the shield eats one point, the other two land.
                game.castXSpell(1, "Festival of the Guildpact", 1).error shouldBe null
                game.resolveStack()

                // Player 1 still holds priority after their own spell resolved; hand it over
                // so the opponent can respond with the Bolt.
                game.passPriority()
                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.resolveStack()

                withClue("a 1-point shield absorbs 1 of the Bolt's 3") {
                    game.getLifeTotal(1) shouldBe 18
                }
            }
        }
    }
}
