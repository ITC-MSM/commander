package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DamageDealtEvent
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.engine.core.Outcome

/**
 * CR 701.14b — "If one or both creatures instructed to fight are no longer on the battlefield or are
 * no longer creatures, neither of them fights or deals damage."
 *
 * Exercised through Mind Meanderer (FRA), whose enters trigger reads "it fights up to one target
 * creature an opponent controls": the fighter is the trigger's *source*, not a target, so CR 608.2b
 * never removes it. A Meanderer that has stopped being a creature is still on the battlefield and
 * still the same object, so only the fight executor itself can notice that it may no longer fight.
 */
class FightWithUnavailableFighterScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Casts Mind Meanderer, resolves it, and aims its enters trigger at [foe]; the trigger is left on the stack. */
    fun castMeandererTargeting(driver: GameTestDriver, foe: EntityId): EntityId {
        val meanderer = driver.putCardInHand(driver.player1, "Mind Meanderer")
        driver.giveMana(driver.player1, Color.GREEN, 4)
        driver.giveMana(driver.player1, Color.BLUE, 2)
        driver.castSpell(driver.player1, meanderer).outcome shouldBe Outcome.Done
        driver.bothPass() // resolve the creature spell; its enters trigger asks for its target

        driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitTargetSelection(driver.player1, listOf(foe))
        return meanderer
    }

    fun damageOn(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("the 4/4 Meanderer fights and kills a 2/2, taking 2 back") {
        val driver = newDriver()
        val bears = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")

        val meanderer = castMeandererTargeting(driver, bears)
        driver.bothPass() // resolve the trigger

        driver.findPermanent(driver.player2, "Grizzly Bears") shouldBe null
        driver.findPermanent(driver.player1, "Mind Meanderer") shouldNotBe null
        damageOn(driver, meanderer) shouldBe 2
    }

    test("if the Meanderer leaves before its trigger resolves, neither creature deals damage") {
        val driver = newDriver()
        val bears = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")

        val meanderer = castMeandererTargeting(driver, bears)
        driver.moveToGraveyard(meanderer) // gone in response; the trigger is still on the stack
        val result = driver.bothPass()

        result.events.filterIsInstance<DamageDealtEvent>().shouldBeEmpty()
        driver.findPermanent(driver.player2, "Grizzly Bears") shouldBe bears
        damageOn(driver, bears) shouldBe 0
    }

    test("if the Meanderer is no longer a creature when its trigger resolves, neither deals damage") {
        val driver = newDriver()
        val bears = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")

        val meanderer = castMeandererTargeting(driver, bears)
        // In response, Imprisoned in the Moon turns the Meanderer into a colorless land that is no
        // longer a creature — it stays on the battlefield as the same object.
        val moon = driver.putPermanentOnBattlefield(driver.player2, "Imprisoned in the Moon")
        driver.addComponent(moon, AttachedToComponent(meanderer))
        driver.addComponent(meanderer, AttachmentsComponent(listOf(moon)))
        driver.state.projectedState.isCreature(meanderer) shouldBe false

        val result = driver.bothPass()

        result.events.filterIsInstance<DamageDealtEvent>().shouldBeEmpty()
        driver.findPermanent(driver.player2, "Grizzly Bears") shouldBe bears
        damageOn(driver, bears) shouldBe 0
        damageOn(driver, meanderer) shouldBe 0
    }
})
