package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tests for Ultros, Obnoxious Octopus (FIN #83).
 *
 * Ultros {1}{U} Legendary Creature — Octopus 2/1
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, tap target
 *   creature an opponent controls and put a stun counter on it.
 * Whenever you cast a noncreature spell, if at least eight mana was spent to cast it, put eight
 *   +1/+1 counters on Ultros.
 *
 * Both abilities are intervening-"if"s on mana actually spent. The ≥8 ability is isolated by
 * casting with no opponent creature in play, so the ≥4 ability finds no legal target and is
 * never put on the stack — leaving only the +1/+1 payoff to resolve.
 */
class UltrosObnoxiousOctopusScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        cardRegistry.register(
            CardDefinition.sorcery(
                name = "Test Four Drop",
                manaCost = ManaCost.parse("{4}"),
                oracleText = "You gain 1 life.",
                script = CardScript(spellEffect = Effects.GainLife(1))
            )
        )
        cardRegistry.register(
            CardDefinition.sorcery(
                name = "Test Eight Drop",
                manaCost = ManaCost.parse("{8}"),
                oracleText = "You gain 1 life.",
                script = CardScript(spellEffect = Effects.GainLife(1))
            )
        )

        test("a four-mana noncreature spell taps and stuns an opponent creature, no +1/+1 counters") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Ultros, Obnoxious Octopus")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInHand(1, "Test Four Drop")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ultros = game.findPermanent("Ultros, Obnoxious Octopus")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Test Four Drop").error shouldBe null
            game.resolveStack()
            withClue("The ≥4 ability should pause to choose its opponent-creature target") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectTargets(listOf(bears))
            game.resolveStack()

            withClue("Opponent's Grizzly Bears is tapped") {
                game.state.getEntity(bears)!!.has<TappedComponent>() shouldBe true
            }
            withClue("Opponent's Grizzly Bears has a stun counter") {
                (game.state.getEntity(bears)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0) shouldBe 1
            }
            withClue("Four mana is below the eight-mana threshold — no +1/+1 counters on Ultros") {
                stateProjector.project(game.state).getPower(ultros) shouldBe 2
            }
        }

        test("an eight-mana noncreature spell puts eight +1/+1 counters on Ultros") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Ultros, Obnoxious Octopus")
                .withLandsOnBattlefield(1, "Island", 8)
                .withCardInHand(1, "Test Eight Drop")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ultros = game.findPermanent("Ultros, Obnoxious Octopus")!!

            // No opponent creature exists, so the ≥4 "tap target" ability has no legal target and
            // is not put on the stack. Only the ≥8 ability resolves.
            game.castSpell(1, "Test Eight Drop").error shouldBe null
            game.resolveStack()
            if (game.state.stack.isNotEmpty()) game.resolveStack()

            withClue("Eight +1/+1 counters land on Ultros (2/1 -> 10/9)") {
                (game.state.getEntity(ultros)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 8
                stateProjector.project(game.state).getPower(ultros) shouldBe 10
                stateProjector.project(game.state).getToughness(ultros) shouldBe 9
            }
        }
    }
}
