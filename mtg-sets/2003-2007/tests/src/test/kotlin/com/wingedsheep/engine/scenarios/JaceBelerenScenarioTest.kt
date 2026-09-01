package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.JaceBeleren
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.state.ZoneKey
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Jace Beleren {1}{U}{U} — Legendary Planeswalker — Jace (loyalty 3)
 *   +2: Each player draws a card.
 *   −1: Target player draws a card.
 *   −10: Target player mills twenty cards.
 *
 * The +2 is symmetric, so the test checks *both* hands grew — an "each opponent" slip would leave
 * Jace's controller unchanged. The −1 is aimed at the opponent, the one shape where a defaulted
 * "you draw" disagrees with the card.
 */
class JaceBelerenScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(JaceBeleren))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun putJace(driver: GameTestDriver, playerId: EntityId, loyalty: Int): EntityId {
        val jace = driver.putPermanentOnBattlefield(playerId, "Jace Beleren")
        driver.addComponent(jace, CountersComponent(mapOf(CounterType.LOYALTY to loyalty)))
        return jace
    }

    fun loyalty(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    fun librarySize(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getZone(ZoneKey(playerId, Zone.LIBRARY)).size

    val abilities = JaceBeleren.script.activatedAbilities

    test("+2 has each player draw a card") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val jace = putJace(driver, me, 3)
        val myHand = driver.getHandSize(me)
        val theirHand = driver.getHandSize(opp)

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = jace, abilityId = abilities[0].id))
        driver.bothPass()

        withClue("Jace's controller drew too") { driver.getHandSize(me) shouldBe myHand + 1 }
        withClue("and so did the opponent") { driver.getHandSize(opp) shouldBe theirHand + 1 }
        loyalty(driver, jace) shouldBe 5
    }

    test("−1 has only the targeted player draw") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val jace = putJace(driver, me, 3)
        val myHand = driver.getHandSize(me)
        val theirHand = driver.getHandSize(opp)

        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = jace,
                abilityId = abilities[1].id,
                targets = listOf(ChosenTarget.Player(opp))
            )
        )
        driver.bothPass()

        withClue("the targeted opponent drew") { driver.getHandSize(opp) shouldBe theirHand + 1 }
        withClue("Jace's controller did not") { driver.getHandSize(me) shouldBe myHand }
        loyalty(driver, jace) shouldBe 2
    }

    test("−10 mills the targeted player twenty cards") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val jace = putJace(driver, me, 10)
        val theirLibrary = librarySize(driver, opp)
        val theirGraveyard = driver.getGraveyard(opp).size

        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = jace,
                abilityId = abilities[2].id,
                targets = listOf(ChosenTarget.Player(opp))
            )
        )
        driver.bothPass()

        librarySize(driver, opp) shouldBe theirLibrary - 20
        driver.getGraveyard(opp).size shouldBe theirGraveyard + 20
        loyalty(driver, jace) shouldBe 0
    }
})
