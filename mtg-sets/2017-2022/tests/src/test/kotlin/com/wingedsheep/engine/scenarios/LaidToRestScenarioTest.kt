package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Laid to Rest (VOW #207).
 *
 * "{3}{G} Enchantment
 *  Whenever a Human you control dies, draw a card.
 *  Whenever a creature you control with a +1/+1 counter on it dies, you gain 2 life."
 *
 * Exercises the two independent LKI-filtered dies triggers (CR 603.10 — the filter reads the
 * dying creature's captured state):
 *  - a Human you control dying draws a card (and, without a counter, does NOT gain life);
 *  - a non-Human creature with a +1/+1 counter dying gains 2 life (and does NOT draw);
 *  - a Human *with* a +1/+1 counter dying fires BOTH (draw AND gain 2 life);
 *  - a Human an *opponent* controls dying fires neither (the `youControl` filter).
 */
class LaidToRestScenarioTest : ScenarioTestBase() {

    init {
        /** Directly add +1/+1 counters to a battlefield entity without going through the stack. */
        fun addPlusOne(game: TestGame, id: EntityId, count: Int) {
            game.state = game.state.updateEntity(id) { container ->
                val existing = container.get<CountersComponent>() ?: CountersComponent()
                container.with(existing.withAdded(CounterType.PLUS_ONE_PLUS_ONE, count))
            }
        }

        context("Laid to Rest") {

            test("a Human you control dying draws a card (no counter → no life gain)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Laid to Rest")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2 Human Soldier
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val human = game.findPermanent("Glory Seeker")!!
                val handBefore = game.handSize(1)
                val lifeBefore = game.getLifeTotal(1)

                // Lightning Bolt (3) kills the 2/2 Human.
                game.castSpell(1, "Lightning Bolt", human).error shouldBe null
                game.resolveStack()

                withClue("Human is dead") { game.findPermanent("Glory Seeker") shouldBe null }
                // Hand: -1 (bolt cast) +1 (Laid to Rest draw) = net unchanged from before-cast.
                withClue("drew a card from the Human dying") { game.handSize(1) shouldBe handBefore }
                withClue("no +1/+1 counter → no life gain") { game.getLifeTotal(1) shouldBe lifeBefore }
            }

            test("a non-Human creature with a +1/+1 counter dying gains 2 life (no draw)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Laid to Rest")
                    .withCardOnBattlefield(1, "Grizzly Bears") // 2/2 Bear — not a Human
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                addPlusOne(game, bear, 1) // 3/3 with a +1/+1 counter — dies to Bolt's 3.
                val handBefore = game.handSize(1)

                game.castSpell(1, "Lightning Bolt", bear).error shouldBe null
                game.resolveStack()

                withClue("Bear is dead") { game.findPermanent("Grizzly Bears") shouldBe null }
                withClue("gained 2 life from the counter-bearing creature dying") {
                    game.getLifeTotal(1) shouldBe 22
                }
                // Hand: -1 (bolt), no draw (not a Human).
                withClue("no draw — the dying creature was not a Human") {
                    game.handSize(1) shouldBe handBefore - 1
                }
            }

            test("a Human with a +1/+1 counter dying fires both triggers") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Laid to Rest")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2 Human Soldier
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val human = game.findPermanent("Glory Seeker")!!
                addPlusOne(game, human, 1) // 3/3 Human with a +1/+1 counter — dies to Bolt's 3.
                val handBefore = game.handSize(1)

                game.castSpell(1, "Lightning Bolt", human).error shouldBe null
                game.resolveStack()

                withClue("Human is dead") { game.findPermanent("Glory Seeker") shouldBe null }
                withClue("drew a card (Human died)") { game.handSize(1) shouldBe handBefore }
                withClue("gained 2 life (counter-bearing creature died)") {
                    game.getLifeTotal(1) shouldBe 22
                }
            }

            test("a Human an opponent controls dying fires neither trigger") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Laid to Rest")
                    .withCardOnBattlefield(2, "Glory Seeker") // opponent's Human
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val oppHuman = game.findPermanent("Glory Seeker")!!
                val handBefore = game.handSize(1)

                game.castSpell(1, "Lightning Bolt", oppHuman).error shouldBe null
                game.resolveStack()

                withClue("opponent's Human is dead") { game.findPermanent("Glory Seeker") shouldBe null }
                // Hand: -1 (bolt), no draw — the dying Human wasn't yours.
                withClue("no draw — the Human was not one you control") {
                    game.handSize(1) shouldBe handBefore - 1
                }
                withClue("no life gain") { game.getLifeTotal(1) shouldBe 20 }
            }
        }
    }
}
