package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import com.wingedsheep.sdk.dsl.decayed

/**
 * Scenario tests for Rot-Curse Rakshasa (TDM #87) — {1}{B} Demon, 5/5.
 *
 * "Trample
 *  Decayed (This creature can't block. When it attacks, sacrifice it at end of combat.)
 *  Renew — {X}{B}{B}, Exile this card from your graveyard: Put a decayed counter on each of
 *  X target creatures. Activate only as a sorcery."
 *
 * Exercises both the printed keywords and the new decayed-counter mechanic (CR 702.147a): the
 * Renew ability mints decayed counters on X targets, and a creature bearing a decayed counter
 * (a) can't block and (b) is sacrificed at end of combat if it attacks.
 */
class RotCurseRakshasaScenarioTest : ScenarioTestBase() {

    private val renewAbilityId =
        cardRegistry.getCard("Rot-Curse Rakshasa")!!.activatedAbilities.first().id

    init {
        context("Rot-Curse Rakshasa") {

            test("the printed creature has trample and decayed (can't block)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rot-Curse Rakshasa")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rakshasa = game.findPermanent("Rot-Curse Rakshasa")!!
                withClue("Rot-Curse Rakshasa has Trample") {
                    game.state.projectedState.hasKeyword(rakshasa, Keyword.TRAMPLE) shouldBe true
                }
                withClue("Decayed grants the can't-block static (CR 702.147a)") {
                    game.state.projectedState.cantBlock(rakshasa) shouldBe true
                }
            }

            test("renew puts a decayed counter on each of X target creatures") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Rot-Curse Rakshasa")
                    .withCardOnBattlefield(2, "Glory Seeker")  // 2/2
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2 (second target)
                    .withLandsOnBattlefield(1, "Swamp", 4)    // {X=2}{B}{B}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rakshasa = game.findCardsInGraveyard(1, "Rot-Curse Rakshasa").first()
                val glorySeeker = game.findPermanent("Glory Seeker")!!
                val grizzlyBears = game.findPermanent("Grizzly Bears")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rakshasa,
                        abilityId = renewAbilityId,
                        xValue = 2,
                        targets = listOf(
                            ChosenTarget.Permanent(glorySeeker),
                            ChosenTarget.Permanent(grizzlyBears),
                        ),
                    )
                )
                withClue("Activating Renew should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Glory Seeker gets one decayed counter") {
                    (game.state.getEntity(glorySeeker)?.get<CountersComponent>()
                        ?.getCount(CounterType.DECAYED) ?: 0) shouldBe 1
                }
                withClue("Grizzly Bears gets one decayed counter") {
                    (game.state.getEntity(grizzlyBears)?.get<CountersComponent>()
                        ?.getCount(CounterType.DECAYED) ?: 0) shouldBe 1
                }
                withClue("The decayed counter grants the Decayed keyword via projection") {
                    game.state.projectedState.hasKeyword(glorySeeker, Keyword.DECAYED) shouldBe true
                }
                withClue("A creature with a decayed counter can't block") {
                    game.state.projectedState.cantBlock(glorySeeker) shouldBe true
                }
                withClue("Rot-Curse Rakshasa is exiled from the graveyard as part of the cost") {
                    game.findCardsInGraveyard(1, "Rot-Curse Rakshasa").size shouldBe 0
                    game.state.getExile(game.player1Id).contains(rakshasa) shouldBe true
                }
            }

            test("a creature with a decayed counter is sacrificed at end of combat if it attacks") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Glory Seeker", tapped = false, summoningSickness = false)
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val attacker = game.findPermanent("Glory Seeker")!!
                // Stamp a decayed counter directly (as Renew would).
                game.state = game.state.updateEntity(attacker) {
                    it.with(CountersComponent(mapOf(CounterType.DECAYED to 1)))
                }

                withClue("The decayed-countered creature can attack") {
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    val result = game.declareAttackers(mapOf("Glory Seeker" to 2))
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("It is still alive during combat") {
                    game.isOnBattlefield("Glory Seeker") shouldBe true
                }

                // Pass out of combat — the delayed end-of-combat trigger fires and sacrifices it.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                withClue("The attacking decayed creature is sacrificed at end of combat (CR 702.147a)") {
                    game.isOnBattlefield("Glory Seeker") shouldBe false
                    game.findCardsInGraveyard(1, "Glory Seeker").size shouldBe 1
                }
            }
        }
    }
}
