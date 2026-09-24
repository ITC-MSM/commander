package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Warrior's Blades (FRA #163) — {2}{R}{W} Artifact — Equipment.
 *
 *   When this Equipment enters, it deals 3 damage to any target and you gain 3 life.
 *   Equipped creature gets +2/+1.
 *   Equip {3}. This ability costs {1} less to activate for each +1/+1 counter on the creature it
 *   targets.
 */
class WarriorsBladesScenarioTest : ScenarioTestBase() {

    private fun TestGame.addPlusOneCounters(id: EntityId, count: Int) {
        state = state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, count))
        }
    }

    private fun TestGame.equip(blades: EntityId, creature: EntityId) = execute(
        ActivateAbility(
            playerId = player1Id,
            sourceId = blades,
            abilityId = cardRegistry.getCard("Warrior's Blades")!!.activatedAbilities.first().id,
            targets = listOf(ChosenTarget.Permanent(creature)),
        )
    )

    private fun ScenarioBuilder.equipBoard(untappedLands: Int): ScenarioBuilder = this
        .withPlayers("Player1", "Player2")
        .withLandsOnBattlefield(1, "Plains", untappedLands)
        .withCardOnBattlefield(1, "Warrior's Blades")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    init {
        context("Warrior's Blades") {

            test("enters: 3 damage to any target and you gain 3 life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInHand(1, "Warrior's Blades")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Warrior's Blades").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("the enters trigger asks for its target") { (decision is ChooseTargetsDecision) shouldBe true }
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 17
                game.getLifeTotal(1) shouldBe 23
            }

            test("equip onto a creature with two +1/+1 counters costs only {1}") {
                val game = scenario().equipBoard(untappedLands = 1).build()
                val blades = game.findPermanent("Warrior's Blades")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.addPlusOneCounters(bears, 2)

                game.equip(blades, bears).error shouldBe null
                game.resolveStack()

                game.state.getEntity(blades)?.get<AttachedToComponent>()?.targetId shouldBe bears
                withClue("2/2 base + two counters + the Blades' +2/+1") {
                    game.state.projectedState.getPower(bears) shouldBe 6
                    game.state.projectedState.getToughness(bears) shouldBe 5
                }
            }

            test("equip onto a creature with no counters costs the full {3}") {
                val game = scenario().equipBoard(untappedLands = 2).build()
                val blades = game.findPermanent("Warrior's Blades")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("two mana is short of the unreduced {3}") {
                    game.equip(blades, bears).error shouldNotBe null
                }
                game.state.getEntity(blades)?.get<AttachedToComponent>() shouldBe null
            }

            test("the discount bottoms out at {0} — four counters equips for free") {
                val game = scenario().equipBoard(untappedLands = 0).build()
                val blades = game.findPermanent("Warrior's Blades")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.addPlusOneCounters(bears, 4)

                game.equip(blades, bears).error shouldBe null
                game.resolveStack()

                game.state.getEntity(blades)?.get<AttachedToComponent>()?.targetId shouldBe bears
            }
        }
    }
}
