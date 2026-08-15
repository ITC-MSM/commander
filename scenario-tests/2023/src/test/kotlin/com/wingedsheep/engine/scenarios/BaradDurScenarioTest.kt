package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.CreaturesDiedThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.BaradDur
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Barad-dûr — "{X}{X}{B}, {T}: Amass Orcs X. Activate only if a creature died this turn." Exercises
 * the {X}-in-an-activated-cost amass (the chosen X threads into `DynamicAmount.XValue`) and the
 * `Conditions.CreatureDiedThisTurn` activation gate.
 */
class BaradDurScenarioTest : FunSpec({

    val projector = StateProjector()
    val amassAbilityId = BaradDur.activatedAbilities[1].id // {X}{X}{B}, {T}: Amass Orcs X

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
    }

    fun GameTestDriver.orcArmies(player: EntityId): List<EntityId> {
        val projected = projector.project(state)
        return projected.getBattlefieldControlledBy(player)
            .filter { projected.isCreature(it) && projected.hasSubtype(it, "Army") }
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("amasses Orcs X when a creature died this turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val barad = d.putLandOnBattlefield(you, "Barad-dûr")
        d.addComponent(you, CreaturesDiedThisTurnComponent(1)) // gate satisfied
        d.giveMana(you, Color.BLACK, 5) // {X}{X}{B} at X=2 → 4 generic + {B}

        d.orcArmies(you).size shouldBe 0
        val result = d.submit(
            ActivateAbility(playerId = you, sourceId = barad, abilityId = amassAbilityId, xValue = 2)
        )
        result.isSuccess shouldBe true
        d.bothPass() // resolve the amass

        val army = d.orcArmies(you).single()
        projector.project(d.state).hasSubtype(army, "Orc") shouldBe true
        d.plusOneCounters(army) shouldBe 2 // Amass Orcs 2 → two +1/+1 counters
    }

    test("cannot activate the amass ability unless a creature died this turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val barad = d.putLandOnBattlefield(you, "Barad-dûr")
        d.giveMana(you, Color.BLACK, 5)
        // No creature has died this turn — the activation restriction must reject it.

        val result = d.submit(
            ActivateAbility(playerId = you, sourceId = barad, abilityId = amassAbilityId, xValue = 2)
        )
        result.isSuccess shouldBe false
        d.orcArmies(you).size shouldBe 0
    }
})
