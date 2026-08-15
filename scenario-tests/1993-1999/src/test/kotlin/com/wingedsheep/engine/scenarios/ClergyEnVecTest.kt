package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.ClergyEnVec
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Clergy en-Vec — auto-generated from the mtgish IR by mtgish-tooling
 * (the `CreateFutureReplaceWouldDealDamage` mapping) and promoted with this scenario test.
 *
 * Clergy en-Vec: {1}{W}
 * Creature — Human Cleric
 * 1/1
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 */
class ClergyEnVecTest : FunSpec({

    val abilityId = ClergyEnVec.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("prevents the next 1 damage to target creature") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val clergy = driver.putCreatureOnBattlefield(activePlayer, "Clergy en-Vec")
        val target = driver.putCreatureOnBattlefield(activePlayer, "Hill Giant")
        driver.removeSummoningSickness(clergy)

        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = clergy,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(target))
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // Lightning Bolt deals 3 — the shield prevents 1, so 2 lands.
        driver.giveMana(activePlayer, Color.RED, 1)
        val bolt = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.castSpellWithTargets(activePlayer, bolt, listOf(ChosenTarget.Permanent(target)))
        driver.bothPass()

        val damage = driver.state.getEntity(target)?.get<DamageComponent>()?.amount ?: 0
        damage shouldBe 2
    }

    test("prevents the next 1 damage to target player") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val clergy = driver.putCreatureOnBattlefield(activePlayer, "Clergy en-Vec")
        driver.removeSummoningSickness(clergy)

        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = clergy,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.giveMana(activePlayer, Color.RED, 1)
        val bolt = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.castSpellWithTargets(activePlayer, bolt, listOf(ChosenTarget.Player(opponent)))
        driver.bothPass()

        driver.getLifeTotal(opponent) shouldBe 18
    }
})
