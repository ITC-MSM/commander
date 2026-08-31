package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Runo Stromkirk // Krothuss, Lord of the Deep (VOW #246).
 *
 *   Front — Runo Stromkirk (1/4) — Flying. When Runo enters, put up to one target creature card
 *           from your graveyard on top of your library. At the beginning of your upkeep, look at
 *           the top card of your library; you may reveal it, and if a creature card with mana value
 *           6 or greater is revealed this way, transform Runo.
 *   Back  — Krothuss, Lord of the Deep (3/5) — Flying. Whenever Krothuss attacks, create a tapped
 *           and attacking token that's a copy of another target attacking creature. If that creature
 *           is a Kraken, Leviathan, Octopus, or Serpent, create two of those tokens instead.
 *
 * The interesting pieces are the two conditionals. The upkeep flip is gated on the *revealed*
 * card's type **and** mana value, so a revealed cheap creature must not flip it; and Krothuss's
 * token count is a `DynamicAmount.Conditional` over the target's subtypes, so the same effect makes
 * one token or two depending purely on what it copied.
 */
class RunoStromkirkScenarioTest : ScenarioTestBase() {

    init {
        context("Runo Stromkirk") {

            /** Runo on the battlefield with [topOfLibrary] as the only card in the library. */
            fun runoWithTop(topOfLibrary: String): TestGame =
                scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Runo Stromkirk", summoningSickness = false)
                    .withCardInLibrary(1, topOfLibrary)
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

            fun faceName(game: TestGame): String =
                game.state.getEntity(
                    game.findPermanent("Runo Stromkirk") ?: game.findPermanent("Krothuss, Lord of the Deep")!!
                )!!.get<CardComponent>()!!.name

            /** Run the upkeep trigger, revealing the looked-at card when offered the choice. */
            fun runUpkeepRevealing(game: TestGame) {
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                var guard = 0
                while (guard++ < 20) {
                    val decision = game.getPendingDecision()
                    when {
                        decision is com.wingedsheep.engine.core.SelectCardsDecision ->
                            game.selectCards(listOf(decision.options.first()))
                        game.state.stack.isNotEmpty() -> game.resolveStack()
                        else -> break
                    }
                }
            }

            test("revealing a creature with mana value 6 or greater transforms Runo") {
                val game = runoWithTop("Colossal Dreadmaw") // {4}{G}{G} — mana value 6
                runUpkeepRevealing(game)
                withClue("a six-drop creature was revealed") {
                    faceName(game) shouldBe "Krothuss, Lord of the Deep"
                }
            }

            test("revealing a cheap creature does not transform Runo") {
                val game = runoWithTop("Grizzly Bears") // mana value 2
                runUpkeepRevealing(game)
                withClue("the filter tests mana value as well as card type") {
                    faceName(game) shouldBe "Runo Stromkirk"
                }
            }

            test("the enters trigger tucks a chosen creature card from your graveyard on top") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Runo Stromkirk")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardInGraveyard(1, "Colossal Dreadmaw")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dreadmaw = game.findCardsInGraveyard(1, "Colossal Dreadmaw").single()

                game.castSpell(1, "Runo Stromkirk").error shouldBe null
                var guard = 0
                while (guard++ < 20) {
                    when {
                        game.getPendingDecision() is SelectManaSourcesDecision -> game.submitManaSourcesAutoPay()
                        game.hasPendingDecision() -> game.selectTargets(listOf(dreadmaw))
                        game.state.stack.isNotEmpty() -> game.resolveStack()
                        else -> break
                    }
                }

                withClue("the Dreadmaw left the graveyard for the top of the library") {
                    game.isInGraveyard(1, "Colossal Dreadmaw") shouldBe false
                    game.state.getLibrary(game.player1Id).first() shouldBe dreadmaw
                }
            }

            test("declining the reveal leaves Runo on its front face") {
                val game = runoWithTop("Colossal Dreadmaw")
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                var guard = 0
                while (guard++ < 20) {
                    val decision = game.getPendingDecision()
                    when {
                        decision is com.wingedsheep.engine.core.SelectCardsDecision -> game.skipSelection()
                        game.state.stack.isNotEmpty() -> game.resolveStack()
                        else -> break
                    }
                }
                withClue("the transform reads the *revealed* collection, which stayed empty") {
                    faceName(game) shouldBe "Runo Stromkirk"
                }
            }
        }

        context("Krothuss, Lord of the Deep") {

            /** Krothuss plus one other creature of the caller's choosing, ready to attack. */
            fun krothussWith(partner: String): TestGame =
                scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Krothuss, Lord of the Deep", summoningSickness = false)
                    .withCardOnBattlefield(1, partner, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

            fun attackAndCopy(game: TestGame, partner: String) {
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf("Krothuss, Lord of the Deep" to 2, partner to 2)
                ).error shouldBe null
                var guard = 0
                while (guard++ < 20) {
                    when {
                        game.hasPendingDecision() -> {
                            val partnerId = game.findPermanent(partner)!!
                            game.selectTargets(listOf(partnerId))
                        }
                        game.state.stack.isNotEmpty() -> game.resolveStack()
                        else -> break
                    }
                }
            }

            test("copying a non-Kraken attacker makes one token") {
                val partner = "Grizzly Bears"
                val game = krothussWith(partner)
                attackAndCopy(game, partner)
                withClue("one original plus one token copy") {
                    game.findAllPermanents(partner).size shouldBe 2
                }
            }

            test("copying an Octopus attacker makes two tokens") {
                val partner = "Giant Octopus" // Creature — Octopus
                val game = krothussWith(partner)
                attackAndCopy(game, partner)
                withClue("the subtype rider doubles the count: one original plus two token copies") {
                    game.findAllPermanents(partner).size shouldBe 3
                }
            }
        }
    }
}
