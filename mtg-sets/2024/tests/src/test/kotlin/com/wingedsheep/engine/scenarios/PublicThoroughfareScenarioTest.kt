package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Public Thoroughfare (MKM #265) — Land.
 *
 * "This land enters tapped.
 *  When this land enters, sacrifice it unless you tap an untapped artifact or land you control.
 *  {T}: Add one mana of any color."
 *
 * The "unless" is a cost paid on resolution of the enters trigger, not a may-gate and not a target,
 * so the three outcomes are: pay and keep it, decline and lose it, or have nothing to pay with and
 * lose it without ever being asked. All three are covered.
 *
 * The filter carries only the type union — `Costs.pay.Tap` already means "untapped, yours" — and it
 * carries **no self-exclusion**, which the last test pins: the 2024-02-02 ruling calls out that an
 * untapped Public Thoroughfare may tap *itself* to pay. `PayOrSufferExecutor` used to drop the
 * source from the candidate set regardless of the atom's `excludeSelf`, which silently outlawed
 * that line; it now reads the flag, matching `CostPaymentService`. The engine-level coverage for
 * both halves of that fix lives in `PayOrSufferSelfExclusionTest`.
 */
class PublicThoroughfareScenarioTest : ScenarioTestBase() {

    private fun ScenarioTestBase.TestGame.isTapped(id: EntityId): Boolean =
        state.getEntity(id)?.get<TappedComponent>() != null

    init {
        context("Public Thoroughfare") {

            test("tapping another land keeps it, and it entered tapped") {
                val game = scenario()
                    .withPlayers("Traveller", "Opponent")
                    .withCardInHand(1, "Public Thoroughfare")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val land = game.findCardsInHand(1, "Public Thoroughfare").single()
                game.execute(com.wingedsheep.engine.core.PlayLand(game.player1Id, land))
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("the unless-clause raises a selection over the payable permanents") {
                    (decision as? SelectCardsDecision)?.options.orEmpty() shouldContain forest
                }
                game.selectCards(listOf(forest))

                withClue("paying keeps the land") {
                    game.findPermanent("Public Thoroughfare") shouldBe land
                }
                withClue("and the Forest is now tapped — that was the payment") {
                    game.isTapped(forest) shouldBe true
                }
                withClue("the Thoroughfare itself entered tapped by replacement") {
                    game.isTapped(land) shouldBe true
                }
            }

            test("declining sacrifices it") {
                val game = scenario()
                    .withPlayers("Traveller", "Opponent")
                    .withCardInHand(1, "Public Thoroughfare")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val land = game.findCardsInHand(1, "Public Thoroughfare").single()
                game.execute(com.wingedsheep.engine.core.PlayLand(game.player1Id, land))
                game.resolveStack()
                game.selectCards(emptyList())

                withClue("declining the cost means suffering the sacrifice") {
                    game.findPermanent("Public Thoroughfare") shouldBe null
                    game.isInGraveyard(1, "Public Thoroughfare") shouldBe true
                }
            }

            test("with nothing untapped to tap, it is sacrificed without a prompt") {
                val game = scenario()
                    .withPlayers("Traveller", "Opponent")
                    .withCardInHand(1, "Public Thoroughfare")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val land = game.findCardsInHand(1, "Public Thoroughfare").single()
                game.execute(com.wingedsheep.engine.core.PlayLand(game.player1Id, land))
                game.resolveStack()

                withClue("an unpayable cost is never offered") {
                    game.getPendingDecision() shouldBe null
                }
                withClue("and the land goes straight to the graveyard") {
                    game.isInGraveyard(1, "Public Thoroughfare") shouldBe true
                }
            }

            test("an untapped Thoroughfare may tap itself to pay") {
                val game = scenario()
                    .withPlayers("Traveller", "Opponent")
                    .withCardInHand(1, "Public Thoroughfare")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val land = game.findCardsInHand(1, "Public Thoroughfare").single()
                game.execute(com.wingedsheep.engine.core.PlayLand(game.player1Id, land))

                // Untap it before the trigger resolves — the unusual case the 2024-02-02 ruling
                // describes, where tapping itself is a legal payment.
                game.state = game.state.updateEntity(game.findPermanent("Public Thoroughfare")!!) {
                    it.without<TappedComponent>()
                }
                withClue("the untap took, so the ruling's precondition genuinely holds") {
                    game.isTapped(game.findPermanent("Public Thoroughfare")!!) shouldBe false
                }
                game.resolveStack()

                val thoroughfare = game.findPermanent("Public Thoroughfare")
                withClue(
                    "it is its own legal payment — the cost atom's excludeSelf is false, and the " +
                        "executor honours it rather than dropping the source unconditionally"
                ) {
                    thoroughfare shouldBe land
                    val decision = game.getPendingDecision() as? SelectCardsDecision
                    decision?.options.orEmpty() shouldContain land
                }
                game.selectCards(listOf(land))

                withClue("paying with itself keeps it on the battlefield, tapped") {
                    game.findPermanent("Public Thoroughfare") shouldBe land
                    game.isTapped(land) shouldBe true
                }
            }
        }
    }
}
