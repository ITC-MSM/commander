package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
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

            test("begin combat: with a power-7+ creature, offers and completes the free cast of the exiled card") {
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

                withClue("accepting grants a may-play-without-cost permission for the exiled card") {
                    val exiledBears = game.state.getExile(game.player1Id).single()
                    game.state.mayPlayPermissions.flatMap { it.cardIds } shouldNotBe emptyList<Any>()
                    game.state.mayPlayPermissions.flatMap { it.cardIds }.contains(exiledBears) shouldBe true
                }

                // Grizzly Bears is a creature (sorcery speed, CR 117.1a) — the granted permission is
                // an ordinary "may play" grant (CR 118.9), not "as though it had flash",
                // so it can't actually be cast during the beginning-of-combat step it was offered in.
                // It's only usable once a main phase with an empty stack comes around again — here,
                // the postcombat main phase later this same turn.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                val cast = game.castSpellFromExile(1, "Grizzly Bears")
                withClue("the granted permission lets Grizzly Bears be cast from exile for free: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears resolved onto the battlefield without paying its mana cost") {
                    game.findPermanent("Grizzly Bears") shouldNotBe null
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
}
