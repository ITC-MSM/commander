package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Fight Rigging (SNC #145) — {2}{G} Enchantment.
 *
 *   Hideaway 5 (When this enchantment enters, look at the top five cards of your library, exile
 *   one face down, then put the rest on the bottom in a random order.)
 *   At the beginning of combat on your turn, put a +1/+1 counter on target creature you control.
 *   Then if you control a creature with power 7 or greater, you may play the exiled card without
 *   paying its mana cost.
 *
 * Covers: Hideaway exiles exactly one of the top five cards on ETB, the begin-combat trigger
 * always adds the +1/+1 counter to its target, and the free-cast offer only appears (and can
 * actually be completed) once a power-7-or-greater creature is controlled after the counter
 * resolves.
 */
class FightRiggingScenarioTest : ScenarioTestBase() {

    init {
        context("Fight Rigging") {

            test("Hideaway exiles exactly one of the top five library cards on ETB") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fight Rigging")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Sol Ring")
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Fight Rigging").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as? SelectCardsDecision
                withClue("Hideaway pauses to choose one of the top five cards to exile face down") {
                    decision shouldNotBe null
                    decision!!.options.size shouldBe 5
                    decision.minSelections shouldBe 1
                    decision.maxSelections shouldBe 1
                }

                game.selectCards(listOf(decision!!.options.first()))

                withClue("exactly one card is exiled, and Fight Rigging resolved onto the battlefield") {
                    game.state.getExile(game.player1Id).size shouldBe 1
                    game.findPermanent("Fight Rigging") shouldNotBe null
                }
                withClue("the other four go to the bottom of the library: net library size -1") {
                    game.librarySize(1) shouldBe 4
                }
            }

            test("begin combat: adds the +1/+1 counter; without a power-7+ creature, no free-play offer") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fight Rigging")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // 2/2
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Sol Ring")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                resolveHideaway(game)

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                if (game.getPendingDecision() is ChooseTargetsDecision) game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("the +1/+1 counter always lands on the (mandatory) target") {
                    game.state.getEntity(bears)!!.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("a 3-power Grizzly Bears doesn't clear the power-7 bar: no free-play offer") {
                    game.getPendingDecision() shouldBe null
                }
            }

            test("begin combat: accepting casts the exiled card immediately during resolution") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fight Rigging")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardOnBattlefield(1, "Ghalta, Primal Hunger", summoningSickness = false) // 12/12
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Sol Ring")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ghalta = game.findPermanent("Ghalta, Primal Hunger")!!
                // resolveHideaway always exiles the top library card — Grizzly Bears here — so the
                // free cast below has a known, targetless card to replay.
                resolveHideaway(game)

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                if (game.getPendingDecision() is ChooseTargetsDecision) game.selectTargets(listOf(ghalta))
                game.resolveStack()

                withClue("the +1/+1 counter lands on Ghalta") {
                    game.state.getEntity(ghalta)!!.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("controlling a 13-power creature offers the free-cast of the exiled card") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe true
                }

                game.answerYesNo(true)
                game.resolveStack()

                withClue("accepting casts Grizzly Bears during the beginning-of-combat trigger") {
                    game.state.getExile(game.player1Id) shouldBe emptyList()
                    game.state.step shouldBe Step.BEGIN_COMBAT
                    game.findPermanent("Grizzly Bears") shouldNotBe null
                }
            }

            test("begin combat: declining leaves no permission to cast the exiled card later") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fight Rigging")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardOnBattlefield(1, "Ghalta, Primal Hunger", summoningSickness = false)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Sol Ring")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ghalta = game.findPermanent("Ghalta, Primal Hunger")!!
                resolveHideaway(game)
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                if (game.getPendingDecision() is ChooseTargetsDecision) game.selectTargets(listOf(ghalta))
                game.resolveStack()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("declining leaves the card exiled and grants no lingering permission") {
                    game.state.getExile(game.player1Id).size shouldBe 1
                    game.state.mayPlayPermissions.flatMap { it.cardIds } shouldBe emptyList()
                }

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                withClue("the declined card cannot be cast later in the turn") {
                    game.castSpellFromExile(1, "Grizzly Bears").error shouldNotBe null
                }
            }

            test("begin combat: an exiled land is played immediately and consumes the land play") {
                val game = fightRiggingWithHiddenForest()
                val ghalta = game.findPermanent("Ghalta, Primal Hunger")!!
                resolveHideaway(game)

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                if (game.getPendingDecision() is ChooseTargetsDecision) game.selectTargets(listOf(ghalta))
                game.resolveStack()
                game.answerYesNo(true)

                withClue("the Forest is played during resolution, despite combat timing") {
                    game.state.getBattlefield(game.player1Id).count {
                        game.state.getEntity(it)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe 4
                    game.state.getExile(game.player1Id) shouldBe emptyList()
                    game.state.step shouldBe Step.BEGIN_COMBAT
                }
            }

            test("begin combat: an exiled land stays exiled when the land play was already used") {
                val game = fightRiggingWithHiddenForest(includeLandInHand = true)
                val ghalta = game.findPermanent("Ghalta, Primal Hunger")!!
                resolveHideaway(game)
                val handLand = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Plains"
                }
                game.execute(PlayLand(game.player1Id, handLand)).error shouldBe null

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                if (game.getPendingDecision() is ChooseTargetsDecision) game.selectTargets(listOf(ghalta))
                game.resolveStack()
                game.answerYesNo(true)

                withClue("Fight Rigging cannot provide an additional land play") {
                    game.state.getExile(game.player1Id).size shouldBe 1
                    game.state.mayPlayPermissions.flatMap { it.cardIds } shouldBe emptyList()
                }
            }
        }
    }

    /** Cast Fight Rigging from hand and resolve Hideaway, exiling the first of the top five cards. */
    private fun resolveHideaway(game: TestGame) {
        game.castSpell(1, "Fight Rigging").error shouldBe null
        game.resolveStack()
        val decision = game.getPendingDecision() as? SelectCardsDecision
            ?: error("expected a hideaway selection, got ${game.getPendingDecision()}")
        game.selectCards(listOf(decision.options.first()))
    }

    private fun fightRiggingWithHiddenForest(includeLandInHand: Boolean = false): TestGame {
        val builder = scenario()
            .withPlayers("Player1", "Player2")
            .withCardInHand(1, "Fight Rigging")
            .withLandsOnBattlefield(1, "Forest", 3)
            .withCardOnBattlefield(1, "Ghalta, Primal Hunger", summoningSickness = false)
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(1, "Hill Giant")
            .withCardInLibrary(1, "Sol Ring")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Swamp")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        if (includeLandInHand) builder.withCardInHand(1, "Plains")
        return builder.build()
    }
}
