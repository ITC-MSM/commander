package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Deep Forest Hermit (MH1 #161).
 *
 * {3}{G}{G} Creature — Elf Druid 1/1
 * "Vanishing 3
 *  When this creature enters, create four 1/1 green Squirrel creature tokens.
 *  Squirrels you control get +1/+1."
 *
 * Vanishing (CR 702.62) has no engine executor — `Keyword.VANISHING` and
 * `KeywordAbility.vanishing(n)` exist for rendering only — so the card spells its three printed
 * abilities out of existing primitives. These tests are the proof that the composition behaves:
 * the Hermit enters with three time counters, sheds exactly one per *its controller's* upkeep, and
 * is sacrificed when the last one leaves. The Squirrel lord is asserted alongside, because the
 * tokens it makes are the reason the countdown matters.
 *
 * The countdown test drives a real [GameTestDriver] game rather than a static scenario board: it
 * spans four of the controller's turns, so both players need real libraries to draw from.
 */
class DeepForestHermitScenarioTest : ScenarioTestBase() {

    init {
        fun timeCounters(game: TestGame, id: EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.TIME) ?: 0

        fun timeCounters(driver: GameTestDriver, id: EntityId): Int =
            driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.TIME) ?: 0

        context("Deep Forest Hermit — entry") {

            test("enters with three time counters and brings four Squirrels, each a 2/2 under the lord") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Deep Forest Hermit")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Deep Forest Hermit").error shouldBe null
                game.resolveStack()

                val hermit = game.findPermanent("Deep Forest Hermit")
                withClue("Deep Forest Hermit resolved onto the battlefield") {
                    (hermit != null) shouldBe true
                }
                withClue("Vanishing 3 — it enters with three time counters on it") {
                    timeCounters(game, hermit!!) shouldBe 3
                }

                val squirrels = game.findAllPermanents("Squirrel Token")
                withClue("The enters trigger creates four 1/1 green Squirrel tokens") {
                    squirrels.size shouldBe 4
                }
                withClue("\"Squirrels you control get +1/+1\" makes each token a 2/2") {
                    squirrels.forEach { token ->
                        game.state.projectedState.getPower(token) shouldBe 2
                        game.state.projectedState.getToughness(token) shouldBe 2
                    }
                }
                withClue("The Hermit is an Elf Druid, so the lord never pumps the Hermit itself") {
                    game.state.projectedState.getPower(hermit!!) shouldBe 1
                }
            }
        }

        context("Deep Forest Hermit — the vanishing countdown") {

            test("sheds one time counter per upkeep and is sacrificed when the last one is removed") {
                val driver = GameTestDriver()
                driver.registerCards(TestCards.all)
                driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
                driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

                val controller = driver.activePlayer!!
                driver.giveMana(controller, Color.GREEN, 5)
                val cardId = driver.putCardInHand(controller, "Deep Forest Hermit")
                driver.submit(CastSpell(controller, cardId, emptyList())).isSuccess shouldBe true
                driver.bothPass()

                val hermit = driver.findPermanent(controller, "Deep Forest Hermit")
                withClue("Deep Forest Hermit resolved onto the battlefield") {
                    (hermit != null) shouldBe true
                }
                withClue("It entered with three time counters") {
                    timeCounters(driver, hermit!!) shouldBe 3
                }

                /** Roll forward to the controller's next precombat main, past their own upkeep. */
                fun advanceToOwnNextMain() {
                    driver.passPriorityUntil(Step.CLEANUP)
                    driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // opponent's turn
                    driver.passPriorityUntil(Step.CLEANUP)
                    driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // controller's next turn
                }

                advanceToOwnNextMain()
                withClue("The controller's first upkeep after it entered: 3 → 2") {
                    timeCounters(driver, hermit!!) shouldBe 2
                }

                advanceToOwnNextMain()
                withClue("Second upkeep: 2 → 1") {
                    timeCounters(driver, hermit!!) shouldBe 1
                }
                withClue("It is still on the battlefield while a time counter remains") {
                    (driver.findPermanent(controller, "Deep Forest Hermit") != null) shouldBe true
                }

                advanceToOwnNextMain()
                withClue("Third upkeep removes the last counter and the reflexive trigger sacrifices it") {
                    driver.findPermanent(controller, "Deep Forest Hermit").shouldBeNull()
                }
                withClue("The Squirrels outlive it — and are 1/1 again with the lord gone") {
                    val squirrels = driver.getPermanents(controller).filter { id ->
                        driver.state.getEntity(id)?.get<CardComponent>()?.name == "Squirrel Token"
                    }
                    squirrels.size shouldBe 4
                    squirrels.forEach { token ->
                        driver.state.projectedState.getPower(token) shouldBe 1
                    }
                }
            }
        }
    }
}
