package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.GhostTown
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull

/**
 * Ghost Town (TMP #318)
 * Land
 * {T}: Add {C}.
 * {0}: Return this land to its owner's hand. Activate only if it's not your turn.
 */
class GhostTownScenarioTest : FunSpec({

    val manaAbilityId = GhostTown.activatedAbilities[0].id
    val bounceAbilityId = GhostTown.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GhostTown)
        return driver
    }

    test("the mana ability adds {C}") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val town = driver.putPermanentOnBattlefield(activePlayer, "Ghost Town")

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = town, abilityId = manaAbilityId)
        )
        result.isSuccess shouldBe true
        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.colorless shouldBe 1
    }

    test("the {0} bounce CANNOT be activated on its controller's own turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val town = driver.putPermanentOnBattlefield(activePlayer, "Ghost Town")

        driver.submitExpectFailure(
            ActivateAbility(playerId = activePlayer, sourceId = town, abilityId = bounceAbilityId)
        )

        // Still on the battlefield.
        driver.findPermanent(activePlayer, "Ghost Town") shouldNotBe null
    }

    test("the {0} bounce CAN be activated when it's not the controller's turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        // Opponent controls the Ghost Town; it is the active player's turn, not the opponent's.
        val town = driver.putPermanentOnBattlefield(opponent, "Ghost Town")

        // Advance so the opponent gets priority during the active player's turn.
        driver.passPriority(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = opponent, sourceId = town, abilityId = bounceAbilityId)
        )
        result.isSuccess shouldBe true
        // Let the ability resolve.
        driver.bothPass()

        // Ghost Town left the battlefield and is back in the opponent's hand.
        driver.findPermanent(opponent, "Ghost Town").shouldBeNull()
        driver.findCardInHand(opponent, "Ghost Town") shouldNotBe null
    }
})
