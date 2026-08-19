package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.nph.cards.Dismember
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Scenario tests for Dismember (NPH #57) — {1}{B/P}{B/P} Instant. */
class DismemberScenarioTest : ScenarioTestBase() {
    init {
        context("Dismember") {
            test("kills a small creature by reducing its toughness to 0 or less") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dismember")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.castSpell(1, "Dismember", giant).error shouldBe null
                game.resolveStack()

                withClue("-5/-5 kills a 3/3") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInGraveyard(2, "Hill Giant") shouldBe true
                }
            }

            test("a high-toughness creature survives at reduced stats") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dismember")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Wall of Stone")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Wall of Stone")!!
                game.castSpell(1, "Dismember", wall).error shouldBe null
                game.resolveStack()

                withClue("a 0/8 survives -5/-5 as a -5/3") {
                    game.isOnBattlefield("Wall of Stone") shouldBe true
                    game.state.projectedState.getPower(wall) shouldBe -5
                    game.state.projectedState.getToughness(wall) shouldBe 3
                }
            }

            test("casts with one mana by paying 4 life for both Phyrexian pips") {
                val driver = GameTestDriver().apply {
                    registerCards(TestCards.all)
                    registerCard(Dismember)
                    initMirrorMatch(Deck.of("Swamp" to 40), startingLife = 20)
                }
                val caster = driver.activePlayer!!
                val opponent = driver.getOpponent(caster)
                val swamp = driver.putLandOnBattlefield(caster, "Swamp")
                val target = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
                val spell = driver.putCardInHand(caster, "Dismember")
                driver.submit(
                    CastSpell(
                        playerId = caster,
                        cardId = spell,
                        targets = listOf(ChosenTarget.Permanent(target)),
                        paymentStrategy = PaymentStrategy.Explicit(
                            manaAbilitiesToActivate = listOf(swamp),
                            phyrexianLifePayments = listOf(Color.BLACK, Color.BLACK)
                        )
                    )
                ).isSuccess shouldBe true

                driver.getLifeTotal(caster) shouldBe 16
                driver.bothPass().isSuccess shouldBe true
                driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
            }
        }
    }
}
