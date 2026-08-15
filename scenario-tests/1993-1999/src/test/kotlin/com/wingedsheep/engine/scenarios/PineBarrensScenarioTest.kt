package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.PineBarrens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pine Barrens (TMP #321)
 * Land — enters tapped
 * {T}: Add {C}.
 * {T}: Add {B} or {G}. This land deals 1 damage to you.
 */
class PineBarrensScenarioTest : FunSpec({

    val colorlessAbilityId = PineBarrens.activatedAbilities[0].id
    val blackAbilityId = PineBarrens.activatedAbilities[1].id
    val greenAbilityId = PineBarrens.activatedAbilities[2].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PineBarrens)
        return driver
    }

    test("the {B} ability adds black mana and deals 1 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val barrens = driver.putPermanentOnBattlefield(activePlayer, "Pine Barrens")
        driver.untapPermanent(barrens)
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = barrens, abilityId = blackAbilityId)
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.black shouldBe 1
        driver.getLifeTotal(activePlayer) shouldBe (before - 1)
    }

    test("the {G} ability adds green mana and deals 1 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val barrens = driver.putPermanentOnBattlefield(activePlayer, "Pine Barrens")
        driver.untapPermanent(barrens)
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = barrens, abilityId = greenAbilityId)
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.green shouldBe 1
        driver.getLifeTotal(activePlayer) shouldBe (before - 1)
    }

    test("the {C} ability adds colorless mana and deals NO damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val barrens = driver.putPermanentOnBattlefield(activePlayer, "Pine Barrens")
        driver.untapPermanent(barrens)
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = barrens, abilityId = colorlessAbilityId)
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.colorless shouldBe 1
        driver.getLifeTotal(activePlayer) shouldBe before
    }
})
