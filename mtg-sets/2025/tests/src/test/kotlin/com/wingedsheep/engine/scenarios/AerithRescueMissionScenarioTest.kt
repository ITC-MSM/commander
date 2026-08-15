package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.AerithRescueMission
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Aerith Rescue Mission — {3}{W} Sorcery, modal "Choose one —".
 *
 * Mode A (Take the Elevator): create three 1/1 colorless Hero creature tokens.
 * Mode B (Take 59 Flights of Stairs): tap up to three target creatures, then put a stun
 *        counter on one of them.
 *
 * Modeled on ModalCastTimeModeTest (cast-time mode selection via `chosenModes` +
 * `modeTargetsOrdered`).
 */
class AerithRescueMissionScenarioTest : FunSpec({

    val Bear = CardDefinition.creature(
        name = "Test Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = emptySet(),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Bear))
        driver.registerCard(AerithRescueMission)
        return driver
    }

    // {3}{W} — four white mana covers the {W} plus three generic.
    fun giveWhiteMana(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) {
        driver.giveMana(player, Color.WHITE, 4)
    }

    test("Mode A — Take the Elevator creates three 1/1 colorless Hero tokens") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        giveWhiteMana(driver, activePlayer)
        val spell = driver.putCardInHand(activePlayer, "Aerith Rescue Mission")

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = spell,
                chosenModes = listOf(0)
            )
        )
        result.isSuccess shouldBe true

        driver.bothPass()

        val heroTokens = driver.getCreatures(activePlayer).filter { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.name == "Hero Token"
        }
        heroTokens.size shouldBe 3
        heroTokens.forEach { id ->
            val stats = driver.state.getEntity(id)?.get<CardComponent>()?.baseStats
            stats?.basePower shouldBe 1
            stats?.baseToughness shouldBe 1
        }
    }

    test("Mode B — Take 59 Flights of Stairs taps up to three creatures and stuns one") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bear1 = driver.putCreatureOnBattlefield(opponent, "Test Bear")
        val bear2 = driver.putCreatureOnBattlefield(opponent, "Test Bear")
        val bear3 = driver.putCreatureOnBattlefield(opponent, "Test Bear")

        // All three start untapped.
        driver.isTapped(bear1) shouldBe false
        driver.isTapped(bear2) shouldBe false
        driver.isTapped(bear3) shouldBe false

        giveWhiteMana(driver, activePlayer)
        val spell = driver.putCardInHand(activePlayer, "Aerith Rescue Mission")

        val chosenTargets = listOf(
            ChosenTarget.Permanent(bear1),
            ChosenTarget.Permanent(bear2),
            ChosenTarget.Permanent(bear3)
        )

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = spell,
                targets = chosenTargets,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(chosenTargets)
            )
        )
        result.isSuccess shouldBe true

        driver.bothPass()

        // Resolution pauses so the controller picks WHICH of the tapped creatures gets the
        // stun counter (per ruling, chosen at resolution). Choose bear2 explicitly.
        val decision = driver.state.pendingDecision
        (decision is SelectCardsDecision) shouldBe true
        driver.submitCardSelection(activePlayer, listOf(bear2))

        // All three targeted creatures are tapped.
        driver.isTapped(bear1) shouldBe true
        driver.isTapped(bear2) shouldBe true
        driver.isTapped(bear3) shouldBe true

        // Exactly one stun counter was placed, on the creature the controller chose (bear2).
        fun stun(id: com.wingedsheep.sdk.model.EntityId) =
            driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0
        stun(bear2) shouldBe 1
        listOf(bear1, bear2, bear3).sumOf { stun(it) } shouldBe 1
    }

    test("Mode B — works with a single target (stun lands on it)") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bear = driver.putCreatureOnBattlefield(opponent, "Test Bear")

        giveWhiteMana(driver, activePlayer)
        val spell = driver.putCardInHand(activePlayer, "Aerith Rescue Mission")

        val chosenTargets = listOf(ChosenTarget.Permanent(bear))

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = spell,
                targets = chosenTargets,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(chosenTargets)
            )
        )
        result.isSuccess shouldBe true

        driver.bothPass()

        // With a single tapped creature the choice is unambiguous; answer it if surfaced.
        if (driver.state.pendingDecision is SelectCardsDecision) {
            driver.submitCardSelection(activePlayer, listOf(bear))
        }

        driver.isTapped(bear) shouldBe true
        val stun = driver.state.getEntity(bear)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0
        stun shouldBe 1
    }
})
