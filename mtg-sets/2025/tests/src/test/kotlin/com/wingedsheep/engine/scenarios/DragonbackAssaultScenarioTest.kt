package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dragonback Assault (TDM #179).
 *
 * {3}{G}{U}{R} Enchantment, mythic.
 *   "When this enchantment enters, it deals 3 damage to each creature and each planeswalker.
 *    Landfall — Whenever a land you control enters, create a 4/4 red Dragon creature token
 *    with flying."
 *
 * The ETB sweep iterates the combined GameObjectFilter.CreatureOrPlaneswalker group; the
 * landfall half mirrors Rampaging Baloths (LandYouControlEnters → CreateToken).
 */
class DragonbackAssaultScenarioTest : ScenarioTestBase() {

    init {
        context("enters-the-battlefield sweep") {

            test("deals 3 damage to each creature and each planeswalker") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragonback Assault")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(1, "Glory Seeker")          // 2/2 — dies
                    .withCardOnBattlefield(2, "Hill Giant")            // 3/3 — dies
                    .withCardOnBattlefield(2, "Boulderborn Dragon")    // 3/3 — dies
                    .withCardOnBattlefield(2, "Sorin, Solemn Visitor") // loyalty 4 → 1
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sorin = game.findPermanent("Sorin, Solemn Visitor")!!
                // Seed the planeswalker's starting loyalty (the battlefield builder doesn't add
                // loyalty counters automatically) so the 3-damage delta is observable.
                game.state = game.state.updateEntity(sorin) { container ->
                    container.with(CountersComponent().withAdded(CounterType.LOYALTY, 4))
                }
                game.castSpell(1, "Dragonback Assault").error shouldBe null
                game.resolveStack()

                withClue("Dragonback Assault resolves onto the battlefield") {
                    game.isOnBattlefield("Dragonback Assault") shouldBe true
                }
                withClue("the 2/2 dies to 3 damage") {
                    game.findPermanent("Glory Seeker") shouldBe null
                }
                withClue("both 3/3s die to 3 damage") {
                    game.findPermanent("Hill Giant") shouldBe null
                    game.findPermanent("Boulderborn Dragon") shouldBe null
                }
                withClue("the planeswalker loses 3 loyalty (4 → 1)") {
                    val loyalty = game.state.getEntity(sorin)
                        ?.get<CountersComponent>()
                        ?.getCount(CounterType.LOYALTY) ?: 0
                    loyalty shouldBe 1
                }
            }
        }

        context("landfall") {

            test("creates a 4/4 red Dragon token with flying when a land you control enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dragonback Assault")
                    .withCardInHand(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val before = game.findPermanents("Dragon Token").size

                val forest = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Forest"
                }
                game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("a Dragon token is created on landfall") {
                    game.findPermanents("Dragon Token").size shouldBeGreaterThan before
                }
            }
        }
    }
}
