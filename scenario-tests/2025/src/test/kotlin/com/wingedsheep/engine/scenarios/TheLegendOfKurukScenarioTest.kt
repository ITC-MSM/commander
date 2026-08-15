package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SagaComponent
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.cards.TheLegendOfKuruk
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for The Legend of Kuruk // Avatar Kuruk (TLA #61).
 *
 * Front {2}{U}{U} Enchantment — Saga:
 *   I, II — Scry 2, then draw a card.
 *   III — Exile this Saga, then return it to the battlefield transformed under your control.
 *
 * This pins the card-specific saga wiring: it enters as a Saga with one lore counter and its first
 * chapter triggers. The chapter effect (Scry 2 + draw), the chapter III transform (→ Avatar Kuruk),
 * and the back face's abilities (cast-trigger Spirit token, Exhaust — Waterbend {20} extra turn)
 * reuse machinery proven by The Legend of Kyoshi, Foggy Swamp Spirit Keeper, and the
 * Scry / DrawCards / Exhaust / Waterbend / TakeExtraTurn primitives.
 */
class TheLegendOfKurukScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(TheLegendOfKuruk)

        context("The Legend of Kuruk") {

            test("enters as a Saga with one lore counter and chapter I triggers") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "The Legend of Kuruk")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "The Legend of Kuruk").error shouldBe null
                game.resolveStack()
                // Chapter I (Scry 2, then draw) may pause on the scry selection — keep cards on top.
                if (game.getPendingDecision() is SelectCardsDecision) {
                    game.skipSelection()
                    game.resolveStack()
                }

                val saga = game.findPermanent("The Legend of Kuruk")!!
                val sagaComp = game.state.getEntity(saga)?.get<SagaComponent>()
                withClue("entered as a Saga") { sagaComp shouldNotBe null }
                withClue("one lore counter on entry (CR 714.3a)") {
                    game.state.getEntity(saga)?.get<CountersComponent>()
                        ?.getCount(CounterType.LORE) shouldBe 1
                }
                withClue("chapter I triggered") {
                    sagaComp!!.triggeredChapters.contains(1) shouldBe true
                }
            }

            // Drives the marquee paths the entry test doesn't reach: chapter III's
            // exile-and-return-transformed (→ Avatar Kuruk) and the back face's
            // Exhaust — Waterbend {20} extra-turn ability. Uses GameTestDriver because lore must
            // accrue across real turns (CR 714.3c); chapter I/II scry decisions auto-resolve as the
            // driver passes priority.
            test("chapter III transforms into Avatar Kuruk, whose exhaust takes an extra turn") {
                val projector = StateProjector()
                val driver = GameTestDriver()
                driver.registerCards(TestCards.all + listOf(TheLegendOfKuruk))
                driver.initMirrorMatch(deck = Deck.of("Island" to 40))
                driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
                val you = driver.activePlayer!!
                val opponent = driver.getOpponent(you)

                // Cast the Saga: it enters with one lore counter and chapter I (Scry 2 + draw) fires.
                val spell = driver.putCardInHand(you, "The Legend of Kuruk")
                driver.giveMana(you, Color.BLUE, 2)
                driver.giveColorlessMana(you, 2)
                driver.castSpell(you, spell)
                drain(driver)

                // Accrue lore to 3 over my next two turns → chapter III exiles and returns transformed.
                advanceToNextTurnMain(driver) // opponent's turn
                advanceToNextTurnMain(driver) // my turn → lore 2 (chapter II)
                advanceToNextTurnMain(driver) // opponent's turn
                advanceToNextTurnMain(driver) // my turn → lore 3 (chapter III: transform)

                val kuruk = driver.findPermanent(you, "Avatar Kuruk")
                withClue("chapter III returns the Saga transformed into Avatar Kuruk") {
                    kuruk shouldNotBe null
                }
                val projected = projector.project(driver.state)
                withClue("Avatar Kuruk is a 4/3 creature") {
                    projected.isCreature(kuruk!!) shouldBe true
                    projected.getPower(kuruk) shouldBe 4
                    projected.getToughness(kuruk) shouldBe 3
                }

                // Exhaust — Waterbend {20}: take an extra turn. In a 2-player game an extra turn is
                // modeled by the opponent skipping their next turn (see TimeWarpScenarioTest).
                val exhaust = TheLegendOfKuruk.backFace!!.activatedAbilities.first { it.isExhaust }
                driver.giveColorlessMana(you, 20)
                driver.submit(ActivateAbility(you, kuruk!!, exhaust.id)).isSuccess shouldBe true
                drain(driver)

                withClue("the extra turn makes the opponent skip their next turn") {
                    driver.state.getEntity(opponent)?.has<SkipNextTurnComponent>() shouldBe true
                }
            }
        }
    }

    /** Resolve everything pending — drain the stack and auto-answer any decision (scry, mana). */
    private fun drain(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 60) {
            when {
                driver.pendingDecision != null -> driver.autoResolveDecision()
                driver.state.stack.isNotEmpty() -> driver.bothPass()
                else -> break
            }
        }
    }

    /** Advance to the active player's next precombat main (stepping out via END first). */
    private fun advanceToNextTurnMain(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.END, maxPasses = 300)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)
        drain(driver)
    }
}
