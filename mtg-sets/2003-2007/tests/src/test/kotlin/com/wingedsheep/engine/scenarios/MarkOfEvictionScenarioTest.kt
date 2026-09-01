package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mark of Eviction (RAV #58) — {U} Enchantment — Aura.
 *
 * "Enchant creature. At the beginning of your upkeep, return enchanted creature and all Auras
 * attached to that creature to their owners' hands."
 *
 * Mark of Eviction is itself one of those Auras, so a correct script bounces the card that wrote
 * it. That recursion is what these tests pin, along with the ordering hazard behind it: the Auras
 * are gathered into a collection *before* the host moves, so the later steps never have to read
 * "enchanted creature" off an Aura that is already in hand. Each Aura goes to its own owner's
 * hand, not to the ability controller's.
 */
class MarkOfEvictionScenarioTest : ScenarioTestBase() {

    init {
        context("Mark of Eviction") {

            test("returns the host, the other Auras, and itself to their owners' hands") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Mark of Eviction", "Grizzly Bears")
                    .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")
                    // Start on Bob's turn so the next upkeep reached is Alice's.
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("the enchanted creature goes back to its owner's hand") {
                    game.findPermanents("Grizzly Bears").size shouldBe 0
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
                withClue("the other Aura on it rides along rather than dying unattached") {
                    game.findPermanents("Holy Strength").size shouldBe 0
                    game.isInHand(1, "Holy Strength") shouldBe true
                }
                withClue("Mark of Eviction is attached to that creature too, so it bounces itself") {
                    game.findPermanents("Mark of Eviction").size shouldBe 0
                    game.isInHand(1, "Mark of Eviction") shouldBe true
                }
            }

            test("an opponent's Aura on the same creature goes to that opponent's hand") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Mark of Eviction", "Grizzly Bears")
                    .withCardAttachedTo(2, "Pacifism", "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("\"their owners' hands\" — Bob owns Pacifism, so Bob gets it back") {
                    game.findPermanents("Pacifism").size shouldBe 0
                    game.isInHand(2, "Pacifism") shouldBe true
                    game.isInHand(1, "Pacifism") shouldBe false
                }
            }

            test("it does not fire on the opponent's upkeep") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Mark of Eviction", "Grizzly Bears")
                    // Alice is active, so the next upkeep reached is Bob's.
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("\"your upkeep\" is the Aura controller's, and this one is Bob's") {
                    game.findPermanents("Grizzly Bears").size shouldBe 1
                    game.findPermanents("Mark of Eviction").size shouldBe 1
                }
            }
        }
    }
}
