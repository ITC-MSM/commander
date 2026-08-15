package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Metamorphosis (ARN) — {G} Sorcery.
 *
 * "As an additional cost to cast this spell, sacrifice a creature.
 *  Add X mana of any one color, where X is 1 plus the sacrificed creature's mana value.
 *  Spend this mana only to cast creature spells."
 *
 * This is the first card that pairs a SPELL additional-cost sacrifice with an
 * [com.wingedsheep.sdk.scripting.values.EntityReference.Sacrificed] reference in the
 * resolving effect, so the test pins three things at once:
 *   - the dynamic amount resolves to 1 + the sacrificed creature's mana value, read from
 *     the cost-payment snapshot (the creature is in the graveyard by resolution);
 *   - the player picks a single color and gets X of it;
 *   - every produced mana carries [ManaRestriction.CreatureSpellsOnly].
 */
class MetamorphosisScenarioTest : ScenarioTestBase() {

    init {
        context("Metamorphosis additional-cost sacrifice produces restricted mana") {

            test("sacrificing a mana-value-2 creature yields 3 chosen-color creature-only mana") {
                // Grizzly Bears is {1}{G} → mana value 2, so X = 1 + 2 = 3.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Metamorphosis")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Metamorphosis"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        cardId,
                        emptyList(),
                        additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bears))
                    )
                )
                withClue("Cast sacrificing Grizzly Bears should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }

                // Sacrifice happened up front, before the spell is on the stack.
                game.isInGraveyard(1, "Grizzly Bears") shouldBe true

                game.resolveStack()

                // Resolving the AddManaOfChoice effect pauses for a single color choice.
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<ChooseColorDecision>()
                game.submitDecision(ColorChosenResponse(decision.id, Color.RED))

                val pool = game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!
                withClue("X = 1 + mana value 2 = 3 restricted mana entries") {
                    pool.restrictedMana shouldHaveSize 3
                }
                pool.restrictedMana.forEach { entry ->
                    entry.color shouldBe Color.RED
                    entry.restriction shouldBe ManaRestriction.CreatureSpellsOnly
                }
            }

            test("amount scales with the sacrificed creature's mana value") {
                // Hill Giant is {3}{R} → mana value 4, so X = 1 + 4 = 5.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Metamorphosis")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Metamorphosis"
                }

                game.execute(
                    CastSpell(
                        game.player1Id,
                        cardId,
                        emptyList(),
                        additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(giant))
                    )
                ).error shouldBe null

                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<ChooseColorDecision>()
                game.submitDecision(ColorChosenResponse(decision.id, Color.BLUE))

                val pool = game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!
                withClue("X = 1 + mana value 4 = 5 restricted mana entries") {
                    pool.restrictedMana shouldHaveSize 5
                }
                pool.restrictedMana.forEach { entry ->
                    entry.color shouldBe Color.BLUE
                    entry.restriction shouldBe ManaRestriction.CreatureSpellsOnly
                }
            }

            test("produced mana pays a creature spell but not a noncreature spell") {
                // Sacrifice Hill Giant (mana value 4) → 5 chosen-color creature-only mana.
                // Glasses of Urza is a {1} artifact: red mana could cover its generic cost
                // color-wise, so the only thing stopping it is CreatureSpellsOnly — which is
                // exactly the restriction this test isolates. Hill Giant ({3}{R}) is a creature
                // spell the same pool can pay (including its additional/colored pips).
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Metamorphosis")
                    .withCardInHand(1, "Hill Giant")
                    .withCardInHand(1, "Glasses of Urza")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sacFodder = game.findPermanent("Hill Giant")!!
                val metamorphosis = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Metamorphosis"
                }

                game.execute(
                    CastSpell(
                        game.player1Id,
                        metamorphosis,
                        emptyList(),
                        additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(sacFodder))
                    )
                ).error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<ChooseColorDecision>()
                game.submitDecision(ColorChosenResponse(decision.id, Color.RED))

                // The {G} for Metamorphosis came from the only Forest, so the restricted pool
                // is now the sole mana available.
                val glasses = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Glasses of Urza"
                }
                val noncreatureCast = game.execute(
                    CastSpell(game.player1Id, glasses, emptyList(), paymentStrategy = PaymentStrategy.FromPool)
                )
                withClue("CreatureSpellsOnly mana must NOT pay for the {1} artifact") {
                    noncreatureCast.error shouldNotBe null
                }

                val handGiant = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Hill Giant"
                }
                val creatureCast = game.execute(
                    CastSpell(game.player1Id, handGiant, emptyList(), paymentStrategy = PaymentStrategy.FromPool)
                )
                withClue("CreatureSpellsOnly mana pays the {3}{R} creature spell: ${creatureCast.error}") {
                    creatureCast.error shouldBe null
                }
            }
        }
    }
}
