package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Old Rutstein (VOW #244) — {1}{B}{G} Legendary Creature — Human Peasant, 1/4.
 *
 *   When Old Rutstein enters and at the beginning of your upkeep, mill a card. If a land card is
 *   milled this way, create a Treasure token. If a creature card is milled this way, create a 1/1
 *   green Insect creature token. If a noncreature, nonland card is milled this way, create a Blood
 *   token.
 *
 * The three "If a … card is milled" branches partition every card type, so exactly one fires per
 * mill. Each test rigs a single-card library so only that card can be milled, then casts Old
 * Rutstein and reads which token its ETB produced.
 */
class OldRutsteinScenarioTest : ScenarioTestBase() {

    private fun castRutsteinWithLibrary(topCard: String) = scenario()
        .withPlayers("Player1", "Player2")
        .withCardInHand(1, "Old Rutstein")
        .withLandsOnBattlefield(1, "Swamp", 1)
        .withLandsOnBattlefield(1, "Forest", 1)
        .withLandsOnBattlefield(1, "Mountain", 1) // pays the generic {1}
        .withCardInLibrary(1, topCard) // the only card that can be milled
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        context("Old Rutstein ETB mill-and-react") {

            test("milling a land creates a Treasure token") {
                val game = castRutsteinWithLibrary("Island")

                game.castSpell(1, "Old Rutstein").error shouldBe null
                game.resolveStack()

                withClue("Old Rutstein is on the battlefield") {
                    game.isOnBattlefield("Old Rutstein") shouldBe true
                }
                withClue("the milled land goes to the graveyard") {
                    game.isInGraveyard(1, "Island") shouldBe true
                }
                withClue("milling a land creates exactly one Treasure token") {
                    game.findPermanents("Treasure").size shouldBe 1
                }
                withClue("no creature/Blood branch fired") {
                    game.findPermanents("Blood").size shouldBe 0
                    game.findPermanents("Insect Token").size shouldBe 0
                }
            }

            test("milling a creature creates a 1/1 green Insect token") {
                val game = castRutsteinWithLibrary("Grizzly Bears")

                game.castSpell(1, "Old Rutstein").error shouldBe null
                game.resolveStack()

                withClue("the milled creature goes to the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                val insects = game.findPermanents("Insect Token")
                withClue("milling a creature creates exactly one Insect token") {
                    insects.size shouldBe 1
                }
                withClue("the Insect token is 1/1") {
                    game.state.projectedState.getPower(insects.first()) shouldBe 1
                    game.state.projectedState.getToughness(insects.first()) shouldBe 1
                }
                withClue("no land/noncreature branch fired") {
                    game.findPermanents("Treasure").size shouldBe 0
                    game.findPermanents("Blood").size shouldBe 0
                }
            }

            test("milling a noncreature, nonland card creates a Blood token") {
                val game = castRutsteinWithLibrary("Lightning Bolt")

                game.castSpell(1, "Old Rutstein").error shouldBe null
                game.resolveStack()

                withClue("the milled instant goes to the graveyard") {
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe true
                }
                withClue("milling a noncreature, nonland card creates exactly one Blood token") {
                    game.findPermanents("Blood").size shouldBe 1
                }
                withClue("no land/creature branch fired") {
                    game.findPermanents("Treasure").size shouldBe 0
                    game.findPermanents("Insect Token").size shouldBe 0
                }
            }
        }
    }
}
