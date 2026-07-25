package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for a batch of Wilds of Eldraine cards implemented together. All five compose
 * existing SDK primitives, so these tests pin the *composition* — the parts that could plausibly be
 * wired wrong:
 *
 *  - Ratcatcher Trainee // Pest Problem ({1}{R} 2/1) — a `ConditionalStaticAbility` keyword grant
 *    gated on `IsYourTurn`: first strike must appear and disappear with the turn, not stick.
 *  - Gingerbread Hunter // Puny Snack ({4}{G} 5/5) — ETB Food, and a -2/-2 Adventure that must
 *    actually kill a 2/2.
 *  - Scalding Viper // Steam Clean ({1}{R} 2/1) — a cast watcher with a mana-value threshold; the
 *    threshold and the "opponent only" scoping are the risky bits.
 *  - Pollen-Shield Hare // Hare Raising ({1}{W} 2/2) — a token-only anthem (the nontoken Hare must
 *    not pump itself or nontoken creatures), plus an Adventure whose X counts creatures at
 *    resolution.
 *  - Lord Skitter's Butcher ({2}{B} 2/3) — a three-mode ETB whose middle mode is an
 *    "if you do" sacrifice: declining must cost nothing.
 */
class WoeCardsBatch8ScenarioTest : ScenarioTestBase() {

    init {
        context("Ratcatcher Trainee — first strike only during your turn") {
            test("has first strike on your turn and loses it on the opponent's") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ratcatcher Trainee", summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val trainee = game.findPermanent("Ratcatcher Trainee")!!
                withClue("during your turn") {
                    game.state.projectedState.hasKeyword(trainee, Keyword.FIRST_STRIKE) shouldBe true
                }

                val opponentsTurn = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ratcatcher Trainee", summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val theirTurn = opponentsTurn.findPermanent("Ratcatcher Trainee")!!
                withClue("during the opponent's turn the grant is off") {
                    opponentsTurn.state.projectedState.hasKeyword(theirTurn, Keyword.FIRST_STRIKE) shouldBe false
                }
            }

            test("Pest Problem creates two Rat tokens and exiles the card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Ratcatcher Trainee")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // faceIndex = 0 is the Adventure face; the creature face casts with faceIndex = null.
                val cardId = game.findCardsInHand(1, "Ratcatcher Trainee").first()
                game.execute(CastSpell(game.player1Id, cardId, emptyList(), faceIndex = 0))
                    .isSuccess shouldBe true
                game.resolveStack()

                withClue("two Rats, not one") {
                    game.findAllPermanents("Rat Token").size shouldBe 2
                }
                withClue("resolving the Adventure exiles the card so it can be cast as a creature later") {
                    game.isInExile(1, "Ratcatcher Trainee") shouldBe true
                }
            }
        }

        context("Gingerbread Hunter // Puny Snack") {
            test("the creature face is a 5/5 that makes a Food on entry") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Gingerbread Hunter")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Gingerbread Hunter").error shouldBe null
                game.resolveStack()

                val hunter = game.findPermanent("Gingerbread Hunter")!!
                game.state.projectedState.getPower(hunter) shouldBe 5
                game.state.projectedState.getToughness(hunter) shouldBe 5
                withClue("the ETB made exactly one Food") {
                    game.findAllPermanents("Food").size shouldBe 1
                }
            }

            test("Puny Snack's -2/-2 kills a 2/2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Gingerbread Hunter")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cardId = game.findCardsInHand(1, "Gingerbread Hunter").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Permanent(bears)), faceIndex = 0)
                ).isSuccess shouldBe true
                game.resolveStack()

                withClue("a 2/2 hit by -2/-2 dies to state-based actions") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("the Adventure card is exiled, castable as the Giant later") {
                    game.isInExile(1, "Gingerbread Hunter") shouldBe true
                }
            }
        }

        context("Scalding Viper — 1 damage on an opponent's cheap spell") {
            fun viperGame(opponentCard: String) = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Scalding Viper", summoningSickness = false)
                .withCardInHand(2, opponentCard)
                .withLandsOnBattlefield(2, "Forest", 6)
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            test("an opponent's mana value 2 spell pings them for 1") {
                val game = viperGame("Grizzly Bears")

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("the caster took 1 damage") { game.getLifeTotal(2) shouldBe 19 }
                withClue("the Viper's controller is untouched") { game.getLifeTotal(1) shouldBe 20 }
            }

            test("a mana value 6 spell is above the threshold — no damage") {
                val game = viperGame("Craw Wurm")

                game.castSpell(2, "Craw Wurm").error shouldBe null
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 20
            }

            test("your own cheap spell doesn't trigger it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scalding Viper", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                game.getLifeTotal(1) shouldBe 20
                game.getLifeTotal(2) shouldBe 20
            }

            test("Steam Clean bounces a nonland permanent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Scalding Viper")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cardId = game.findCardsInHand(1, "Scalding Viper").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Permanent(bears)), faceIndex = 0)
                ).isSuccess shouldBe true
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe false
                game.isInHand(2, "Grizzly Bears") shouldBe true
                game.isInExile(1, "Scalding Viper") shouldBe true
            }
        }

        context("Pollen-Shield Hare — the anthem hits tokens only") {
            test("Rat tokens get +1/+1 while the Hare and a nontoken 2/2 do not") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Pollen-Shield Hare", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Ratcatcher Trainee")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val trainee = game.findCardsInHand(1, "Ratcatcher Trainee").first()
                game.execute(CastSpell(game.player1Id, trainee, emptyList(), faceIndex = 0))
                    .isSuccess shouldBe true
                game.resolveStack()

                val rat = game.findPermanent("Rat Token")!!
                withClue("a 1/1 token becomes 2/2") {
                    game.state.projectedState.getPower(rat) shouldBe 2
                    game.state.projectedState.getToughness(rat) shouldBe 2
                }

                val hare = game.findPermanent("Pollen-Shield Hare")!!
                withClue("the Hare is not a token, so it doesn't pump itself") {
                    game.state.projectedState.getPower(hare) shouldBe 2
                    game.state.projectedState.getToughness(hare) shouldBe 2
                }

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("a nontoken creature you control is unaffected") {
                    game.state.projectedState.getPower(bears) shouldBe 2
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
            }

            test("Hare Raising grants vigilance and +X/+X for the creatures you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Pollen-Shield Hare")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cardId = game.findCardsInHand(1, "Pollen-Shield Hare").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Permanent(bears)), faceIndex = 0)
                ).isSuccess shouldBe true
                game.resolveStack()

                withClue("two creatures you control → +2/+2 on a 2/2") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 4
                }
                withClue("…and vigilance") {
                    game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe true
                }
            }
        }

        context("Lord Skitter's Butcher — three-mode ETB") {
            fun butcherGame() = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Lord Skitter's Butcher")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            fun ScenarioTestBase.TestGame.castAndChooseMode(index: Int) {
                castSpell(1, "Lord Skitter's Butcher").error shouldBe null
                if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
                resolveStack()
                val modeDecision = getPendingDecision() as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision for the ETB; got ${getPendingDecision()}")
                submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = index))
            }

            test("mode 0 creates a Rat token") {
                val game = butcherGame()
                game.castAndChooseMode(0)
                game.resolveStack()

                game.findAllPermanents("Rat Token").size shouldBe 1
            }

            test("mode 1 — sacrificing another creature scries 2 and draws") {
                val game = butcherGame()
                val handBefore = game.handSize(1)
                game.castAndChooseMode(1)

                val bears = game.findPermanent("Grizzly Bears")!!
                val sacChoice = game.getPendingDecision() as? SelectCardsDecision
                    ?: error("expected the sacrifice selection; got ${game.getPendingDecision()}")
                withClue("the Butcher itself is not offered — 'another creature'") {
                    sacChoice.options.contains(game.findPermanent("Lord Skitter's Butcher")) shouldBe false
                }
                game.selectCards(listOf(bears)).error shouldBe null

                withClue("the chosen creature was sacrificed") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }

                val scry = game.getPendingDecision() as? SelectCardsDecision
                    ?: error("expected the scry 2 selection; got ${game.getPendingDecision()}")
                withClue("scry 2 looks at the top two cards") { scry.options.size shouldBe 2 }
                game.skipSelection().error shouldBe null // keep both on top
                if (game.getPendingDecision() is ReorderLibraryDecision) game.keepLibraryOrder()
                game.resolveStack()

                withClue("the Butcher (cast from hand) left, and the draw added one card") {
                    game.handSize(1) shouldBe handBefore
                }
                game.librarySize(1) shouldBe 2
            }

            test("mode 1 — declining the sacrifice costs nothing and draws nothing") {
                val game = butcherGame()
                val handBefore = game.handSize(1)
                game.castAndChooseMode(1)

                game.getPendingDecision() shouldNotBe null
                game.skipSelection().error shouldBe null
                game.resolveStack()

                withClue("no sacrifice happened") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("no draw happened — the Butcher just left hand for the battlefield") {
                    game.handSize(1) shouldBe handBefore - 1
                }
                game.librarySize(1) shouldBe 3
            }

            test("mode 2 gives your creatures menace until end of turn") {
                val game = butcherGame()
                game.castAndChooseMode(2)
                game.resolveStack()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.projectedState.hasKeyword(bears, Keyword.MENACE) shouldBe true
                val butcher = game.findPermanent("Lord Skitter's Butcher")!!
                withClue("the Butcher is a creature you control too") {
                    game.state.projectedState.hasKeyword(butcher, Keyword.MENACE) shouldBe true
                }
            }
        }
    }
}
