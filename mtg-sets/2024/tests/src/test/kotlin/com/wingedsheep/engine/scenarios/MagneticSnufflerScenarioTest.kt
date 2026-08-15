package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.`5dn`.cards.KrarkClanIronworks
import com.wingedsheep.mtg.sets.definitions.mkm.cards.ConcealedWeapon
import com.wingedsheep.mtg.sets.definitions.mkm.cards.MagneticSnuffler
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Magnetic Snuffler — the reanimate-and-attach ETB and the sacrifice-an-artifact counter.
 *
 * The ETB is modelled as a two-step composite (put onto the battlefield, then attach to
 * `EffectTarget.Self`) rather than the chosen-host effect, because the printed host is fixed. That
 * only works if the Equipment keeps its entity identity across the graveyard→battlefield move, so
 * the second step can still resolve the same target — which is exactly what the first test pins
 * down: it asserts both the `AttachedToComponent` link *and* the resulting projected power, so a
 * silent no-op in the attach step can't pass.
 */
class MagneticSnufflerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MagneticSnuffler)
        driver.registerCard(ConcealedWeapon)
        driver.registerCard(KrarkClanIronworks)
        return driver
    }

    test("the enters trigger returns an Equipment from the graveyard already attached") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val weapon = driver.putCardInGraveyard(you, "Concealed Weapon")
        val snufflerCard = driver.putCardInHand(you, "Magnetic Snuffler")
        driver.giveColorlessMana(you, 5)
        driver.castSpell(you, snufflerCard).isSuccess shouldBe true
        driver.bothPass() // resolve the creature; its enters trigger goes on the stack

        if (driver.state.pendingDecision != null) {
            driver.submitTargetSelection(you, listOf(weapon))
        }
        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()

        val snuffler = driver.findPermanent(you, "Magnetic Snuffler")!!

        withClue("the Equipment left the graveyard for the battlefield") {
            driver.getGraveyardCardNames(you).contains("Concealed Weapon") shouldBe false
            driver.findPermanent(you, "Concealed Weapon") shouldBe weapon
        }
        withClue("and it arrived attached to the Snuffler, not merely on the battlefield") {
            driver.state.getEntity(weapon)?.get<AttachedToComponent>()?.targetId shouldBe snuffler
        }
        withClue("Concealed Weapon's +3/+0 therefore applies: a 4/4 reads as 7/4") {
            val projected = StateProjector().project(driver.state)
            projected.getPower(snuffler) shouldBe 7
            projected.getToughness(snuffler) shouldBe 4
        }
    }

    test("sacrificing an artifact puts a +1/+1 counter on it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val snuffler = driver.putCreatureOnBattlefield(you, "Magnetic Snuffler")
        val ironworks = driver.putPermanentOnBattlefield(you, "Krark-Clan Ironworks")
        val fodder = driver.putPermanentOnBattlefield(you, "Concealed Weapon")

        withClue("baseline: a printed 4/4 with nothing on it") {
            StateProjector().project(driver.state).getPower(snuffler) shouldBe 4
        }

        val ironworksAbility = KrarkClanIronworks.activatedAbilities.first().id
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = ironworks,
                abilityId = ironworksAbility,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
            )
        )
        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()

        withClue("the sacrifice trigger resolved and the Snuffler grew to 5/5") {
            val projected = StateProjector().project(driver.state)
            projected.getPower(snuffler) shouldBe 5
            projected.getToughness(snuffler) shouldBe 5
        }
    }
})
