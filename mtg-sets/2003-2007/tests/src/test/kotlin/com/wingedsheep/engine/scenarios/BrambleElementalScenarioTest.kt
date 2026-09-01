package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Bramble Elemental (RAV #154) — {3}{G}{G} Creature — Elemental 4/4.
 *
 *   Whenever an Aura becomes attached to this creature, create two 1/1 green Saproling creature
 *   tokens.
 *
 * The trigger watches the *host* side of a `BecomesAttachedEvent`, which is the ANY binding plus
 * `attachedToFilter = Any.sourceItself()` — the first card to pin an attach trigger to its own
 * permanent that way. The failure this guards against is silent in both directions: a binding that
 * stayed SELF would never fire at all, and an `attachedToFilter` the matcher ignored would fire on
 * an Aura landing anywhere on the battlefield. Both are covered below.
 */
class BrambleElementalScenarioTest : ScenarioTestBase() {

    init {
        context("Bramble Elemental") {

            test("an Aura resolving onto it makes two Saprolings") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Bramble Elemental")
                    .withCardInHand(1, "Holy Strength")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elemental = game.findPermanent("Bramble Elemental").shouldNotBeNull()

                game.castSpell(1, "Holy Strength", elemental).error shouldBe null
                game.resolveStack()

                withClue("Two 1/1 green Saprolings enter") {
                    val saprolings = game.findPermanents("Saproling Token")
                    saprolings shouldHaveSize 2
                    saprolings.forEach {
                        game.state.projectedState.getPower(it) shouldBe 1
                        game.state.projectedState.getToughness(it) shouldBe 1
                    }
                }
            }

            test("an Aura attaching to a different creature does not trigger it") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Bramble Elemental")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Holy Strength")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Holy Strength", bears).error shouldBe null
                game.resolveStack()

                withClue("The attached-to filter must pin the host to Bramble Elemental itself") {
                    game.findPermanents("Saproling Token") shouldHaveSize 0
                }
            }

            test("an opponent's Aura triggers it too — the text names no controller") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Bramble Elemental")
                    .withCardInHand(2, "Pacifism")
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elemental = game.findPermanent("Bramble Elemental").shouldNotBeNull()

                game.castSpell(2, "Pacifism", elemental).error shouldBe null
                game.resolveStack()

                withClue("Saprolings still appear, under Bramble Elemental's controller") {
                    val saprolings = game.findPermanents("Saproling Token")
                    saprolings shouldHaveSize 2
                    saprolings.forEach {
                        game.state.projectedState.getController(it) shouldBe game.player1Id
                    }
                }
            }
        }
    }
}
