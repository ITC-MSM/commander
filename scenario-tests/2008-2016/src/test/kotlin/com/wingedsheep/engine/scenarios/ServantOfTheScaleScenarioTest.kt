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
 * Scenario tests for Servant of the Scale (DTK #203).
 *
 * "{G} Creature — Human Soldier 0/0.
 *  This creature enters with a +1/+1 counter on it.
 *  When this creature dies, put X +1/+1 counters on target creature you control, where X is the
 *  number of +1/+1 counters on this creature."
 *
 * Exercises the mtgish coverage tooling's `AsPermanentEnters` -> `EntersWithACounter` mapping (the
 * auto-generated `EntersWithCounters(count = 1, selfOnly = true)` replacement) plus the hand-wired
 * dies-trigger that moves the creature's last-known +1/+1 counters onto a target creature.
 */
class ServantOfTheScaleScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Counterless Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        fun plusOne(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

        context("Servant of the Scale") {

            test("Servant enters with a +1/+1 counter (a 0/0 that survives as a 1/1)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Servant of the Scale")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Servant of the Scale").error shouldBe null
                game.resolveStack()

                val servant = game.findPermanent("Servant of the Scale")
                withClue("Servant is on the battlefield (the +1/+1 counter saved it from the 0/0 SBA)") {
                    servant shouldBe game.findPermanent("Servant of the Scale")
                }
                withClue("Servant enters with one +1/+1 counter") {
                    plusOne(game, servant!!) shouldBe 1
                }
            }

            test("when Servant dies, its +1/+1 counter moves to target creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Servant of the Scale")
                    .withCardOnBattlefield(1, "Counterless Bear", summoningSickness = false)
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Servant of the Scale").error shouldBe null
                game.resolveStack()

                val servant = game.findPermanent("Servant of the Scale")!!
                val bear = game.findPermanent("Counterless Bear")!!
                withClue("Servant has one counter before dying") { plusOne(game, servant) shouldBe 1 }

                // Servant is a 1/1 (0/0 base + one +1/+1). A bolt kills it.
                game.castSpell(1, "Lightning Bolt", servant)
                game.resolveStack()

                withClue("Servant is in the graveyard") { game.findPermanent("Servant of the Scale") shouldBe null }

                // The dies trigger puts the counter on the only valid target (the Bear).
                if (game.hasPendingDecision()) {
                    game.selectTargets(listOf(bear))
                    game.resolveStack()
                }

                withClue("Servant's +1/+1 counter moved onto the Bear") {
                    plusOne(game, bear) shouldBe 1
                }
            }
        }
    }
}
