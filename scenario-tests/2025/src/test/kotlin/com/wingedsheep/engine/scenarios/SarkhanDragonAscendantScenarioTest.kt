package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sarkhan, Dragon Ascendant (TDM #118).
 *
 *  When Sarkhan enters, you may behold a Dragon. If you do, create a Treasure token.
 *  Whenever a Dragon you control enters, put a +1/+1 counter on Sarkhan. Until end of turn,
 *  Sarkhan becomes a Dragon in addition to its other types and gains flying.
 *
 * Exercises the new resolution-time [com.wingedsheep.sdk.scripting.effects.BeholdEffect]
 * (behold-from-hand reveal → Treasure) and the Dragon-enters counter/flying buff.
 */
class SarkhanDragonAscendantScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Dragon",
                manaCost = ManaCost.parse("{R}"),
                subtypes = setOf(Subtype.DRAGON),
                power = 4,
                toughness = 4
            )
        )

        context("Sarkhan, Dragon Ascendant") {

            test("ETB: beholding a Dragon in hand creates a Treasure token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sarkhan, Dragon Ascendant")
                    .withCardInHand(1, "Test Dragon")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Sarkhan, Dragon Ascendant")
                withClue("Casting Sarkhan should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // The ETB behold decision: pick the Dragon card in hand.
                val dragonInHand = game.findCardsInHand(1, "Test Dragon").first()
                withClue("A behold decision should be pending") { game.hasPendingDecision() shouldBe true }
                game.selectCards(listOf(dragonInHand))
                game.resolveStack()

                withClue("A Treasure token should have been created") {
                    game.findPermanents("Treasure").size shouldBe 1
                }
            }

            test("ETB: declining the behold creates no Treasure") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sarkhan, Dragon Ascendant")
                    .withCardInHand(1, "Test Dragon")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Sarkhan, Dragon Ascendant")
                game.resolveStack()

                withClue("A behold decision should be pending") { game.hasPendingDecision() shouldBe true }
                game.skipSelection() // decline to behold
                game.resolveStack()

                withClue("No Treasure when the behold is declined") {
                    game.findPermanents("Treasure").size shouldBe 0
                }
            }

            test("ETB: beholding a Dragon you control (battlefield) creates a Treasure without revealing") {
                // No Dragon in hand — the only behold option is the Dragon already on the
                // battlefield, exercising the "choose a permanent you control" branch (no reveal).
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sarkhan, Dragon Ascendant")
                    .withCardOnBattlefield(1, "Test Dragon", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dragon = game.findPermanent("Test Dragon")!!

                val cast = game.castSpell(1, "Sarkhan, Dragon Ascendant")
                withClue("Casting Sarkhan should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("A behold decision should be pending") { game.hasPendingDecision() shouldBe true }
                game.selectCards(listOf(dragon)) // behold the Dragon we control
                game.resolveStack()

                withClue("Beholding a controlled Dragon creates a Treasure token") {
                    game.findPermanents("Treasure").size shouldBe 1
                }
            }

            test("a Dragon entering puts a +1/+1 counter on Sarkhan and gives him flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sarkhan, Dragon Ascendant", summoningSickness = false)
                    .withCardInHand(1, "Test Dragon")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sarkhan = game.findPermanent("Sarkhan, Dragon Ascendant")!!

                val cast = game.castSpell(1, "Test Dragon")
                withClue("Casting the Dragon should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val counters = game.state.getEntity(sarkhan)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("Sarkhan gains a +1/+1 counter when a Dragon enters") { counters shouldBe 1 }

                withClue("Sarkhan gains flying until end of turn") {
                    game.state.projectedState.hasKeyword(sarkhan, Keyword.FLYING) shouldBe true
                }
                withClue("Sarkhan becomes a Dragon in addition to his other types") {
                    game.state.projectedState.getSubtypes(sarkhan).any { it.equals("Dragon", ignoreCase = true) } shouldBe true
                }
            }
        }
    }
}
