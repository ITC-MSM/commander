package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for City in a Bottle (ARN #60).
 *
 * {2} Artifact
 * "Whenever one or more other nontoken permanents with a name originally printed in the Arabian
 *  Nights expansion are on the battlefield, their controllers sacrifice them.
 *  Players can't cast spells or play lands with a name originally printed in the Arabian Nights
 *  expansion."
 *
 * Three separately-enforced things, so three tests:
 *  - the state trigger (CR 603.8) sweeping the battlefield — and the printed "other", which is
 *    load-bearing here because City in a Bottle's own name was originally printed in ARN;
 *  - the cast lock, read at cast-legality time;
 *  - the land-play lock, which is a *different* enforcement path (playing a land is a special
 *    action, never a cast) and which must stay per-card: the unaffected lands in hand are still
 *    playable.
 *
 * ARN printed only one basic land, Mountain, so every test below draws its mana from Forests and
 * Islands to keep the basics out of the set-membership filter.
 */
class CityInABottleScenarioTest : ScenarioTestBase() {

    init {
        context("City in a Bottle") {

            test("sacrifices every other ARN nontoken permanent, sparing itself and non-ARN cards") {
                val game = scenario()
                    .withPlayers("Bottler", "Victim")
                    .withCardOnBattlefield(1, "City in a Bottle")
                    .withCardOnBattlefield(1, "Kird Ape", summoningSickness = false)
                    .withCardOnBattlefield(2, "Erhnam Djinn", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // The state trigger is polled at a priority pass, goes on the stack, and resolves.
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("Kird Ape (ARN) is sacrificed by its controller") {
                    game.isOnBattlefield("Kird Ape") shouldBe false
                    game.isInGraveyard(1, "Kird Ape") shouldBe true
                }
                withClue("Erhnam Djinn (ARN) is sacrificed by *its* controller, P2") {
                    game.isOnBattlefield("Erhnam Djinn") shouldBe false
                    game.isInGraveyard(2, "Erhnam Djinn") shouldBe true
                }
                withClue("Grizzly Bears (non-ARN) survives") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("City in a Bottle spares itself — the oracle text says *other*") {
                    game.isOnBattlefield("City in a Bottle") shouldBe true
                }
            }

            test("an ARN spell can't be cast, while a non-ARN spell of the same colour still can") {
                val game = scenario()
                    .withPlayers("Bottler", "Caster")
                    .withCardOnBattlefield(1, "City in a Bottle")
                    .withCardInHand(2, "Erhnam Djinn")
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 4)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val castable = game.getLegalActions(2)
                    .filter { it.actionType == "CastSpell" }
                    .map { it.description }
                withClue("Erhnam Djinn is never offered: $castable") {
                    castable.none { it.contains("Erhnam Djinn") } shouldBe true
                }
                withClue("Grizzly Bears is still offered: $castable") {
                    castable.any { it.contains("Grizzly Bears") } shouldBe true
                }

                withClue("and the handler rejects the cast even when the action is submitted directly") {
                    game.castSpell(2, "Erhnam Djinn").error shouldNotBe null
                }

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.resolveStack()
                withClue("the non-ARN spell resolves normally") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("an ARN land can't be played, while a non-ARN land in the same hand still can") {
                val game = scenario()
                    .withPlayers("Bottler", "Lander")
                    .withCardOnBattlefield(1, "City in a Bottle")
                    .withCardInHand(2, "Desert")
                    .withCardInHand(2, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val landDrops = game.getLegalActions(2)
                    .filter { it.actionType == "PlayLand" }
                    .map { it.description }
                withClue("Desert (ARN) is not offered as a land drop: $landDrops") {
                    landDrops.none { it.contains("Desert") } shouldBe true
                }
                withClue("Forest is still offered — a filtered lock must not eat the land drop: $landDrops") {
                    landDrops.any { it.contains("Forest") } shouldBe true
                }

                val desert = game.findCardsInHand(2, "Desert").single()
                withClue("and the handler rejects the play even when submitted directly") {
                    game.execute(PlayLand(game.player2Id, desert)).error shouldNotBe null
                }
                withClue("Desert stays in hand") {
                    game.findCardsInHand(2, "Desert").size shouldBe 1
                }

                val forest = game.findCardsInHand(2, "Forest").single()
                withClue("the unaffected land still drops") {
                    game.execute(PlayLand(game.player2Id, forest)).error shouldBe null
                    game.isOnBattlefield("Forest") shouldBe true
                }
            }
        }
    }
}
