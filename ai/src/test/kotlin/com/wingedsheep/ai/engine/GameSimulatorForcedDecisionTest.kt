package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.mechanics.cost.CostPaymentService
import com.wingedsheep.engine.mechanics.cost.PaymentResult
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class GameSimulatorForcedDecisionTest : FunSpec({

    fun simulateNumberDecision(minValue: Int, maxValue: Int): SimulationResult {
        val testCard = card("Number Choice $minValue-$maxValue") {
            manaCost = "{0}"
            typeLine = "Sorcery"
            spell {
                effect = Effects.ChooseNumberThen(
                    then = Effects.GainLife(1),
                    minValue = minValue,
                    maxValue = maxValue,
                )
            }
        }

        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + testCard)
        driver.initMirrorMatch(Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val cardId = driver.putCardInHand(caster, testCard.name)
        driver.castSpell(caster, cardId).isSuccess.shouldBeTrue()

        val priority = driver.state.priorityPlayerId!!
        return GameSimulator(driver.cardRegistry).simulate(driver.state, PassPriority(priority))
    }

    test("simulator traverses a genuinely forced number decision") {
        simulateNumberDecision(minValue = 4, maxValue = 4)
            .shouldBeInstanceOf<SimulationResult.Terminal>()
    }

    test("simulator stops when the analogous number decision has a real choice") {
        val result = simulateNumberDecision(minValue = 3, maxValue = 4)
            .shouldBeInstanceOf<SimulationResult.NeedsDecision>()

        result.decision.shouldBeInstanceOf<ChooseNumberDecision>()
    }

    test("simulator leaves a solver-suggested mana payment to strategy") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Forest" to 40))

        val payer = driver.player1
        val source = driver.putCreatureOnBattlefield(payer, "Goblin Guide")
        driver.putLandOnBattlefield(payer, "Forest")
        driver.putLandOnBattlefield(payer, "Forest")

        val payment = CostPaymentService(EngineServices(driver.cardRegistry)).pay(
            state = driver.state,
            payerId = payer,
            cost = Costs.pay.Mana(ManaCost.parse("{G}")),
            sourceId = source,
        ).shouldBeInstanceOf<PaymentResult.Pending>()

        val result = GameSimulator(driver.cardRegistry).simulateDecision(
            payment.state,
            YesNoResponse(payment.pendingDecision.id, choice = true),
        ).shouldBeInstanceOf<SimulationResult.NeedsDecision>()

        val manaDecision = result.decision.shouldBeInstanceOf<SelectManaSourcesDecision>()
        manaDecision.availableSources.size shouldBe 2
        manaDecision.autoPaySuggestion.size shouldBe 1
    }
})
