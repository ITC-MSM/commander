package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Floodpits Drowner (DSK #59) — {1}{U} 2/1 Creature — Merfolk.
 *
 * Flash, Vigilance.
 * "When this creature enters, tap target creature an opponent controls and put a stun counter on it."
 * "{1}{U}, {T}: Shuffle this creature and target creature with a stun counter on it into their
 *  owners' libraries."
 *
 * The ETB is the shared "tap + stun" idiom; the activated ability shuffles two permanents (the
 * source plus a stun-countered target) into their owners' libraries.
 */
class FloodpitsDrownerScenarioTest : ScenarioTestBase() {

    private val shuffleAbilityId =
        cardRegistry.getCard("Floodpits Drowner")!!.activatedAbilities.first().id

    init {
        context("Floodpits Drowner — enters-the-battlefield tap + stun") {

            test("ETB taps a target creature an opponent controls and gives it a stun counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Floodpits Drowner")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = false) // 2/2
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Floodpits Drowner").error shouldBe null
                game.resolveStack() // creature enters → ETB asks for a target

                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.selectTargets(listOf(bears))
                withClue("Targeting the opponent's creature is legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("The opponent's creature is tapped") {
                    game.isOnBattlefield("Floodpits Drowner") shouldBe true
                    (game.state.getEntity(bears)?.has<TappedComponent>() == true) shouldBe true
                }
                withClue("The opponent's creature has a stun counter") {
                    (game.state.getEntity(bears)?.get<CountersComponent>()
                        ?.getCount(CounterType.STUN) ?: 0) shouldBe 1
                }
            }
        }

        context("Floodpits Drowner — activated shuffle ability") {

            test("shuffles itself and a stun-countered creature into their owners' libraries") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Floodpits Drowner", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true) // 2/2 with stun counter
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val drowner = game.findPermanent("Floodpits Drowner")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                // Stamp a stun counter on the opposing creature so it is a legal target.
                game.state = game.state.updateEntity(bears) {
                    it.with(CountersComponent(mapOf(CounterType.STUN to 1)))
                }

                val libBeforeP1 = game.librarySize(1)
                val libBeforeP2 = game.librarySize(2)

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = drowner,
                        abilityId = shuffleAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("Activating the shuffle ability should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Floodpits Drowner left the battlefield (shuffled into its owner's library)") {
                    game.isOnBattlefield("Floodpits Drowner") shouldBe false
                    game.librarySize(1) shouldBe libBeforeP1 + 1
                }
                withClue("The stun-countered creature left the battlefield (shuffled into its owner's library)") {
                    game.findPermanents("Grizzly Bears").size shouldBe 0
                    game.librarySize(2) shouldBe libBeforeP2 + 1
                }
            }

            test("cannot target a creature without a stun counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Floodpits Drowner", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = false) // no stun counter
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val drowner = game.findPermanent("Floodpits Drowner")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = drowner,
                        abilityId = shuffleAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("A creature without a stun counter is not a legal target") {
                    (activation.error != null) shouldBe true
                }
            }
        }
    }
}
