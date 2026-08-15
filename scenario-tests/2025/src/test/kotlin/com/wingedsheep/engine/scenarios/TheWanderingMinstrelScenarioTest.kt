package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.GongagaReactorTown
import com.wingedsheep.mtg.sets.definitions.fin.cards.TheWanderingMinstrel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Wandering Minstrel ({G}{U} Legendary Creature — Human Bard, 1/3):
 *   - "Lands you control enter untapped." (EntersUntapped replacement effect)
 *   - "The Minstrel's Ballad" — begin-combat intervening-if (5+ Towns) → 2/2 all-colors Elemental.
 *   - "{3}{W}{U}{B}{R}{G}: Other creatures you control get +X/+X (X = Towns you control)."
 */
class TheWanderingMinstrelScenarioTest : FunSpec({

    fun GameTestDriver.tokenCount(): Int =
        state.getBattlefield().count { state.getEntity(it)?.get<TokenComponent>() != null }

    fun GameTestDriver.resolveAll() {
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard++ < 50) {
            val pd = state.pendingDecision
            if (pd != null) autoResolveDecision() else bothPass()
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TheWanderingMinstrel, GongagaReactorTown))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    // ── "Lands you control enter untapped" ─────────────────────────────────────────────

    test("baseline: a Town tapland played from hand enters tapped without the Minstrel") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val land = driver.putCardInHand(me, "Gongaga, Reactor Town")
        driver.playLand(me, land).isSuccess shouldBe true
        driver.state.getEntity(land)?.has<TappedComponent>() shouldBe true
    }

    test("a Town tapland played from hand enters UNTAPPED while the Minstrel is in play") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "The Wandering Minstrel")

        val land = driver.putCardInHand(me, "Gongaga, Reactor Town")
        driver.playLand(me, land).isSuccess shouldBe true
        driver.state.getEntity(land)?.has<TappedComponent>() shouldBe false
    }

    // ── "The Minstrel's Ballad" begin-combat intervening-if ────────────────────────────

    test("the Ballad creates a token at begin of combat with five or more Towns") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "The Wandering Minstrel")
        repeat(5) { driver.putPermanentOnBattlefield(me, "Gongaga, Reactor Town") }

        driver.tokenCount() shouldBe 0
        driver.passPriorityUntil(Step.BEGIN_COMBAT)
        driver.resolveAll()
        driver.tokenCount() shouldBe 1
    }

    test("the Ballad does NOT trigger with only four Towns (intervening-if)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "The Wandering Minstrel")
        repeat(4) { driver.putPermanentOnBattlefield(me, "Gongaga, Reactor Town") }

        driver.passPriorityUntil(Step.BEGIN_COMBAT)
        driver.resolveAll()
        driver.tokenCount() shouldBe 0
    }

    // ── Activated pump: other creatures get +X/+X where X = Towns you control ───────────

    test("pump gives OTHER creatures you control +X/+X (X = Towns) and not the Minstrel itself") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val minstrel = driver.putCreatureOnBattlefield(me, "The Wandering Minstrel")
        val ally = driver.putCreatureOnBattlefield(me, "First Strike Knight")
        repeat(3) { driver.putPermanentOnBattlefield(me, "Gongaga, Reactor Town") }

        val allyBasePower = driver.state.projectedState.getPower(ally)!!
        val allyBaseToughness = driver.state.projectedState.getToughness(ally)!!
        val minstrelBasePower = driver.state.projectedState.getPower(minstrel)!!

        driver.giveColorlessMana(me, 3)
        driver.giveMana(me, Color.WHITE, 1)
        driver.giveMana(me, Color.BLUE, 1)
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveMana(me, Color.RED, 1)
        driver.giveMana(me, Color.GREEN, 1)

        val abilityId = TheWanderingMinstrel.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = minstrel, abilityId = abilityId))
        driver.resolveAll()

        // X = 3 Towns -> ally gets +3/+3, the Minstrel (source) is excluded.
        driver.state.projectedState.getPower(ally) shouldBe allyBasePower + 3
        driver.state.projectedState.getToughness(ally) shouldBe allyBaseToughness + 3
        driver.state.projectedState.getPower(minstrel) shouldBe minstrelBasePower
    }
})
