package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Spellgorger Weird (WAR #145) — {2}{R} 2/2 Creature — Weird.
 *
 * "Whenever you cast a noncreature spell, put a +1/+1 counter on Spellgorger Weird."
 *
 * Exercises the mtgish `WhenAPlayerCastsASpell` -> `Triggers.YouCastNoncreature` mapping: the
 * trigger fires on each noncreature spell you cast (and accumulates), but NOT on a creature spell.
 */
class SpellgorgerWeirdScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val permanent = game.findPermanent(name)!!
        return game.state.getEntity(permanent)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        cardRegistry.register(
            CardDefinition.instant(
                name = "Test Bolt",
                manaCost = ManaCost.parse("{R}"),
                oracleText = ""
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Bear",
                manaCost = ManaCost.parse("{1}{R}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        context("Spellgorger Weird") {

            test("gains a +1/+1 counter when you cast a noncreature spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spellgorger Weird", summoningSickness = false)
                    .withCardInHand(1, "Test Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Test Bolt").error shouldBe null
                game.resolveStack()

                withClue("casting an instant triggers the noncreature-cast counter") {
                    plusOneCounters(game, "Spellgorger Weird") shouldBe 1
                }
            }

            test("accumulates a counter per noncreature spell cast") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spellgorger Weird", summoningSickness = false)
                    .withCardsInHand(1, "Test Bolt", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Test Bolt").error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Test Bolt").error shouldBe null
                game.resolveStack()

                withClue("the trigger fires for each noncreature spell") {
                    plusOneCounters(game, "Spellgorger Weird") shouldBe 2
                }
            }

            test("does not trigger when you cast a creature spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spellgorger Weird", summoningSickness = false)
                    .withCardInHand(1, "Test Bear")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Test Bear").error shouldBe null
                game.resolveStack()

                withClue("a creature spell must not add a noncreature-cast counter") {
                    plusOneCounters(game, "Spellgorger Weird") shouldBe 0
                }
            }
        }
    }
}
