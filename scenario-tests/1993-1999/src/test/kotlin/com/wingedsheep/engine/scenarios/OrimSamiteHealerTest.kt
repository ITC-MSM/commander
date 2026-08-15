package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.OrimSamiteHealer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Orim, Samite Healer — auto-generated from the mtgish IR by mtgish-tooling
 * (the `CreateFutureReplaceWouldDealDamage` mapping) and promoted with this scenario test.
 *
 * Orim, Samite Healer: {1}{W}{W}
 * Legendary Creature — Human Cleric
 * 1/3
 * {T}: Prevent the next 3 damage that would be dealt to any target this turn.
 */
class OrimSamiteHealerTest : FunSpec({

    val abilityId = OrimSamiteHealer.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("prevents the next 3 damage to target player, the rest lands") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val orim = driver.putCreatureOnBattlefield(activePlayer, "Orim, Samite Healer")
        driver.removeSummoningSickness(orim)

        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = orim,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // First Lightning Bolt (3): the 3-damage shield prevents all of it.
        driver.giveMana(activePlayer, Color.RED, 1)
        val bolt1 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.castSpellWithTargets(activePlayer, bolt1, listOf(ChosenTarget.Player(opponent)))
        driver.bothPass()
        driver.getLifeTotal(opponent) shouldBe 20

        // Second Lightning Bolt (3): the shield is spent, so all 3 lands.
        driver.giveMana(activePlayer, Color.RED, 1)
        val bolt2 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.castSpellWithTargets(activePlayer, bolt2, listOf(ChosenTarget.Player(opponent)))
        driver.bothPass()
        driver.getLifeTotal(opponent) shouldBe 17
    }
})
