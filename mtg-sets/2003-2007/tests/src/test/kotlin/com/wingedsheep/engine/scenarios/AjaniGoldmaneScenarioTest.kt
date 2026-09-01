package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AjaniGoldmane
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ajani Goldmane {2}{W}{W} — Legendary Planeswalker — Ajani (loyalty 4)
 *   +1: You gain 2 life.
 *   −1: Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until
 *       end of turn.
 *   −6: Create a white Avatar creature token. It has "This token's power and toughness are each
 *       equal to your life total."
 *
 * The −1 is aimed at the *opponent's* creature as well, which is the shape where a "you control"
 * filter dropped by mistake would show. The Avatar's P/T is a CDA, so the test moves the life
 * total *after* the token exists and checks the token followed it — a snapshot would not.
 */
class AjaniGoldmaneScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AjaniGoldmane))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun putAjani(driver: GameTestDriver, playerId: EntityId, loyalty: Int): EntityId {
        val ajani = driver.putPermanentOnBattlefield(playerId, "Ajani Goldmane")
        driver.addComponent(ajani, CountersComponent(mapOf(CounterType.LOYALTY to loyalty)))
        return ajani
    }

    fun loyalty(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    fun plusCounters(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    val abilities = AjaniGoldmane.script.activatedAbilities

    test("+1 gains 2 life") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val ajani = putAjani(driver, me, 4)

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = ajani, abilityId = abilities[0].id))
        driver.bothPass()

        driver.getLifeTotal(me) shouldBe 22
        loyalty(driver, ajani) shouldBe 5
    }

    test("−1 puts a +1/+1 counter on each creature you control and grants them vigilance — not the opponent's") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val ajani = putAjani(driver, me, 4)
        val mine1 = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val mine2 = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        val theirs = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = ajani, abilityId = abilities[1].id))
        driver.bothPass()

        val projected = driver.state.projectedState
        withClue("both of my creatures got a counter") {
            plusCounters(driver, mine1) shouldBe 1
            plusCounters(driver, mine2) shouldBe 1
        }
        withClue("and vigilance") {
            projected.hasKeyword(mine1, Keyword.VIGILANCE) shouldBe true
            projected.hasKeyword(mine2, Keyword.VIGILANCE) shouldBe true
        }
        withClue("the opponent's creature is untouched") {
            plusCounters(driver, theirs) shouldBe 0
            projected.hasKeyword(theirs, Keyword.VIGILANCE) shouldBe false
        }
        loyalty(driver, ajani) shouldBe 3
    }

    test("−6 creates a white Avatar whose power and toughness track your life total") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val ajani = putAjani(driver, me, 6)
        driver.setLifeTotal(me, 13)

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = ajani, abilityId = abilities[2].id))
        driver.bothPass()

        val avatars = driver.getCreatures(me).filter { driver.getCardName(it)?.contains("Avatar") == true }
        avatars.size shouldBe 1
        val avatar = avatars.single()
        withClue("the Avatar reads the life total at creation") {
            driver.state.projectedState.getPower(avatar) shouldBe 13
            driver.state.projectedState.getToughness(avatar) shouldBe 13
        }

        driver.setLifeTotal(me, 5)
        withClue("it is a CDA, so it follows the life total afterwards") {
            driver.state.projectedState.getPower(avatar) shouldBe 5
            driver.state.projectedState.getToughness(avatar) shouldBe 5
        }
        loyalty(driver, ajani) shouldBe 0
    }
})
