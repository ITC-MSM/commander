package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.scg.cards.CabalInterrogator
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Cabal Interrogator.
 *
 * Cabal Interrogator
 * {1}{B}
 * Creature — Zombie Wizard
 * 1/1
 * {X}{B}, {T}: Target player reveals X cards from their hand and you choose one of them.
 * That player discards that card. Activate only as a sorcery.
 */
class CabalInterrogatorTest : FunSpec({

    val abilityId = CabalInterrogator.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CabalInterrogator))
        return driver
    }

    test("X=1 with 5 swamps should only tap 2 swamps (1 for B + 1 for X)") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put Cabal Interrogator on battlefield
        val interrogator = driver.putPermanentOnBattlefield(activePlayer, "Cabal Interrogator")
        driver.removeSummoningSickness(interrogator)

        // Put 5 swamps on battlefield
        val swamps = (1..5).map { driver.putLandOnBattlefield(activePlayer, "Swamp") }

        // Give opponent a card in hand so the ability has something to work with
        driver.putCardInHand(opponent, "Swamp")

        // Activate with X=1 targeting opponent
        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = interrogator,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent)),
                xValue = 1
            )
        )
        result.isSuccess shouldBe true

        // Only 2 swamps should be tapped (1 for {B} + 1 for X=1)
        val tappedSwamps = swamps.count { driver.isTapped(it) }
        tappedSwamps shouldBe 2

        // Cabal Interrogator itself should be tapped (part of the cost)
        driver.isTapped(interrogator) shouldBe true

        // Mana pool should be empty (X mana must be deducted, not left floating)
        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        val totalFloating = (pool?.white ?: 0) + (pool?.blue ?: 0) + (pool?.black ?: 0) +
            (pool?.red ?: 0) + (pool?.green ?: 0) + (pool?.colorless ?: 0)
        totalFloating shouldBe 0
    }

    test("X=0 with 5 swamps should only tap 1 swamp (for B)") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val interrogator = driver.putPermanentOnBattlefield(activePlayer, "Cabal Interrogator")
        driver.removeSummoningSickness(interrogator)

        val swamps = (1..5).map { driver.putLandOnBattlefield(activePlayer, "Swamp") }

        // X=0 means reveal 0 cards - but still need to pay {B}
        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = interrogator,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent)),
                xValue = 0
            )
        )
        result.isSuccess shouldBe true

        val tappedSwamps = swamps.count { driver.isTapped(it) }
        tappedSwamps shouldBe 1

        driver.isTapped(interrogator) shouldBe true

        // Mana pool should be empty
        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        val totalFloating = (pool?.white ?: 0) + (pool?.blue ?: 0) + (pool?.black ?: 0) +
            (pool?.red ?: 0) + (pool?.green ?: 0) + (pool?.colorless ?: 0)
        totalFloating shouldBe 0
    }

    test("X=4 with 5 swamps should tap all 5 swamps") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val interrogator = driver.putPermanentOnBattlefield(activePlayer, "Cabal Interrogator")
        driver.removeSummoningSickness(interrogator)

        val swamps = (1..5).map { driver.putLandOnBattlefield(activePlayer, "Swamp") }

        // Give opponent enough cards to reveal
        repeat(4) { driver.putCardInHand(opponent, "Swamp") }

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = interrogator,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent)),
                xValue = 4
            )
        )
        result.isSuccess shouldBe true

        val tappedSwamps = swamps.count { driver.isTapped(it) }
        tappedSwamps shouldBe 5

        driver.isTapped(interrogator) shouldBe true

        // Mana pool should be empty
        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        val totalFloating = (pool?.white ?: 0) + (pool?.blue ?: 0) + (pool?.black ?: 0) +
            (pool?.red ?: 0) + (pool?.green ?: 0) + (pool?.colorless ?: 0)
        totalFloating shouldBe 0
    }
})
