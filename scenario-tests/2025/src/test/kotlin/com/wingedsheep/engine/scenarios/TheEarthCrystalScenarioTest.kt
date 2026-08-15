package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.TheEarthCrystal
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Earth Crystal (FIN #184) — {2}{G}{G} Legendary Artifact.
 *
 *   Green spells you cast cost {1} less to cast.
 *   If one or more +1/+1 counters would be put on a creature you control, twice that many
 *   +1/+1 counters are put on that creature instead.
 *   {4}{G}{G}, {T}: Distribute two +1/+1 counters among one or two target creatures you control.
 *
 * Exercises the activated distribute together with the +1/+1 counter-doubling replacement:
 * the two distributed counters are doubled to four on a creature its controller controls.
 */
class TheEarthCrystalScenarioTest : FunSpec({

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TheEarthCrystal))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    test("the distribute ability's two counters are doubled to four on a creature you control") {
        val (driver, you) = newGame()
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val crystal = driver.putPermanentOnBattlefield(you, "The Earth Crystal")

        driver.giveMana(you, Color.GREEN, 2)
        driver.giveColorlessMana(you, 4)
        val abilityId = TheEarthCrystal.activatedAbilities.first().id
        driver.submit(
            ActivateAbility(you, crystal, abilityId, targets = listOf(ChosenTarget.Permanent(bears)))
        ).isSuccess shouldBe true
        resolveStack(driver)

        // 2 counters distributed onto Grizzly Bears, doubled by The Earth Crystal -> 4.
        plusCounters(driver, bears) shouldBe 4
    }
})
