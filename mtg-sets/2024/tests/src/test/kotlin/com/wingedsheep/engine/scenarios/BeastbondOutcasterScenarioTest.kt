package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Beastbond Outcaster's enter-the-battlefield draw trigger.
 *
 * Oracle: "When this creature enters, if you control a creature with power 4 or greater, draw a
 * card." (Plus Plot {1}{G}, which is the named keyword and not exercised here.)
 *
 * Intervening-if (CR 603.4): the draw only happens if you control a creature with power >= 4 when
 * the trigger resolves. Beastbond Outcaster itself is a 3/3, so it doesn't satisfy its own gate.
 *
 * The third test is the one CR 603.4's *second* check exists for, and the card's own ruling states
 * it: kill the power-4 creature while the trigger is on the stack and the ability is removed from
 * the stack without drawing. It is the mirror image of `SeasonedWarrenguardTest`, whose "while"
 * clause must survive exactly the same interaction — which is why the two conditions live in
 * separate SDK fields.
 */
class BeastbondOutcasterScenarioTest : ScenarioTestBase() {

    init {
        context("Beastbond Outcaster — ETB draw if you control a 4-power creature") {

            test("draws a card when a creature with power 4 or greater is already in play") {
                // Outcaster Trailblazer is a 4/2 (power 4) creature.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Beastbond Outcaster")
                    .withCardOnBattlefield(1, "Outcaster Trailblazer")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size

                game.castSpell(1, "Beastbond Outcaster").error shouldBe null
                game.resolveStack()

                withClue("ETB sees a power-4 creature → draw one card (library -1)") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore - 1
                }
            }

            test("draws nothing when no creature has power 4 or greater") {
                // Beastbond Outcaster is only a 3/3; no other creature in play.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Beastbond Outcaster")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size

                game.castSpell(1, "Beastbond Outcaster").error shouldBe null
                game.resolveStack()

                withClue("no power-4 creature → intervening-if fails, no draw") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore
                }
            }

            test("killing the power-4 creature in response removes the ability from the stack") {
                val kill = card("Test Kill") {
                    manaCost = "{B}"
                    typeLine = "Instant"
                    spell {
                        val t = target("target creature to destroy", Targets.Creature)
                        effect = Effects.Destroy(t)
                    }
                }
                val driver = GameTestDriver()
                driver.registerCards(TestCards.all + listOf(kill))
                driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
                driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
                val active = driver.activePlayer!!

                val trailblazer = driver.putCreatureOnBattlefield(active, "Outcaster Trailblazer")

                driver.giveMana(active, Color.GREEN, 4)
                val outcaster = driver.putCardInHand(active, "Beastbond Outcaster")
                driver.castSpell(active, outcaster)
                driver.bothPass() // the creature spell resolves; the ETB trigger goes on the stack

                // The intervening-"if" was true at trigger time — Outcaster Trailblazer is a 4/2.
                // Destroy it while the ability is still on the stack.
                driver.giveMana(active, Color.BLACK, 1)
                val killSpell = driver.putCardInHand(active, "Test Kill")
                driver.castSpellWithTargets(
                    active, killSpell, listOf(ChosenTarget.Permanent(trailblazer))
                )
                driver.bothPass() // the kill resolves
                driver.findPermanent(active, "Outcaster Trailblazer").shouldBeNull()

                withClue("the ability must still be on the stack, or this test proves nothing") {
                    driver.stackSize shouldBe 1
                }

                val libBefore = driver.state.getLibrary(active).size
                driver.bothPass() // the Outcaster's trigger resolves — or rather, does not
                driver.stackSize shouldBe 0

                withClue("CR 603.4: condition false on resolution → removed from the stack, no draw") {
                    driver.state.getLibrary(active).size shouldBe libBefore
                }
            }
        }
    }
}
