package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.AltarOfShadows
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Altar of Shadows (MRD #143) — "At the beginning of your first main phase, add {B} for each charge
 * counter on this artifact. {7}, {T}: Destroy target creature. Then put a charge counter on this
 * artifact."
 *
 * The card ramps *itself*, so the two halves have to agree: the activated ability must actually add a
 * counter, and the trigger's amount must re-read the counters each turn rather than snapshot a
 * starting value. With zero counters the trigger must add nothing at all — an off-by-one that gave a
 * free {B} would be invisible against any board with counters on it.
 */
class AltarOfShadowsScenarioTest : FunSpec({

    val destroyAbility = AltarOfShadows.activatedAbilities.single().id

    /** Player1's turn, stopped in their upkeep — before the first-main trigger would fire. */
    fun driverAtUpkeep(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + AltarOfShadows)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.UPKEEP)
        return d
    }

    fun driver(): GameTestDriver = driverAtUpkeep().also { it.passPriorityUntil(Step.PRECOMBAT_MAIN) }

    fun GameTestDriver.chargeCounters(altar: EntityId): Int =
        state.getEntity(altar)?.get<CountersComponent>()?.getCount(CounterType.CHARGE) ?: 0

    fun GameTestDriver.blackMana(player: EntityId): Int =
        state.getEntity(player)?.get<ManaPoolComponent>()?.black ?: 0

    test("destroying a creature also puts a charge counter on the altar") {
        val d = driver()
        val altar = d.putPermanentOnBattlefield(d.player1, "Altar of Shadows")
        val bear = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.giveColorlessMana(d.player1, 7)

        d.chargeCounters(altar) shouldBe 0

        d.submit(
            ActivateAbility(d.player1, altar, destroyAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        d.bothPass()

        d.state.getBattlefield().contains(bear) shouldBe false
        withClue("the counter is the second half of the same resolution") {
            d.chargeCounters(altar) shouldBe 1
        }
    }

    test("the first-main trigger adds one {B} per charge counter") {
        val d = driverAtUpkeep()
        val altar = d.putPermanentOnBattlefield(d.player1, "Altar of Shadows")
        // Two activations' worth of counters, without needing two turns to place them.
        d.addComponent(altar, CountersComponent(mapOf(CounterType.CHARGE to 2)))

        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.bothPass()

        withClue("the amount is countersOnSelf, re-read at trigger time") {
            d.blackMana(d.player1) shouldBe 2
        }
    }

    test("with no charge counters the trigger adds nothing") {
        val d = driverAtUpkeep()
        d.putPermanentOnBattlefield(d.player1, "Altar of Shadows")

        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.bothPass()

        d.blackMana(d.player1) shouldBe 0
    }
})
