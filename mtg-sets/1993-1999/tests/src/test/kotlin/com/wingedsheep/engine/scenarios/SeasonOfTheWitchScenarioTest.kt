package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Season of the Witch (DRK #52).
 *
 * {B}{B}{B} Enchantment
 * "At the beginning of your upkeep, sacrifice this enchantment unless you pay 2 life.
 *  At the beginning of the end step, destroy all untapped creatures that didn't attack this turn,
 *  except for creatures that couldn't attack."
 *
 * The end-step sweep is the interesting half: it has to spare three different kinds of creature —
 * the ones that attacked, the ones left tapped, and the ones that never had the option.
 */
class SeasonOfTheWitchScenarioTest : ScenarioTestBase() {

    init {
        context("Season of the Witch") {

            test("the end step destroys a creature that stayed home but spares the attacker") {
                val game = scenario()
                    .withPlayers("Witch", "Opponent")
                    .withCardOnBattlefield(1, "Season of the Witch")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hurloon Minotaur")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers()
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the Minotaur was untapped, could have attacked, and didn't") {
                    game.findPermanent("Hurloon Minotaur").shouldBeNull()
                }
                withClue("the Bears attacked, so they are spared (and are tapped besides)") {
                    game.findPermanent("Grizzly Bears").shouldNotBeNull()
                }
            }

            test("a Wall is spared because it couldn't attack") {
                // Wall of Wood has defender: staying home was never its choice. The Bears next to
                // it have no such excuse.
                val game = scenario()
                    .withPlayers("Witch", "Opponent")
                    .withCardOnBattlefield(1, "Season of the Witch")
                    .withCardOnBattlefield(1, "Wall of Wood")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("defender means it couldn't attack — spared") {
                    game.findPermanent("Wall of Wood").shouldNotBeNull()
                }
                withClue("the Bears could have attacked and didn't") {
                    game.findPermanent("Grizzly Bears").shouldBeNull()
                }
            }

            test("a creature that entered this turn is spared for summoning sickness") {
                // Cast on the same turn the sweep happens, so it never had the option to attack.
                // The Bears that were already there did.
                val game = scenario()
                    .withPlayers("Witch", "Opponent")
                    .withCardOnBattlefield(1, "Season of the Witch")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Hurloon Minotaur")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hurloon Minotaur").error shouldBe null
                game.resolveStack()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("summoning sick, so it couldn't attack — spared") {
                    game.findPermanent("Hurloon Minotaur").shouldNotBeNull()
                }
                withClue("the Bears had been there since before the turn and stayed home") {
                    game.findPermanent("Grizzly Bears").shouldBeNull()
                }
            }

            test("paying the upkeep ransom keeps the enchantment") {
                val game = scenario()
                    .withPlayers("Witch", "Opponent")
                    .withCardOnBattlefield(1, "Season of the Witch")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.ENDING, Step.END)
                    .build()

                // Pass into the Witch player's own upkeep.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                val decision = game.state.pendingDecision
                decision.shouldNotBeNull()
                decision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.resolveStack()

                withClue("paying 2 life keeps the enchantment") {
                    game.findPermanent("Season of the Witch").shouldNotBeNull()
                    game.getLifeTotal(1) shouldBe 18
                }
            }

            test("declining the upkeep ransom sacrifices the enchantment") {
                val game = scenario()
                    .withPlayers("Witch", "Opponent")
                    .withCardOnBattlefield(1, "Season of the Witch")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.ENDING, Step.END)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("refusing the 2 life sacrifices it, and costs nothing") {
                    game.findPermanent("Season of the Witch").shouldBeNull()
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}
