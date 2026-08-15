package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.handlers.effects.FaceDownTurnUp
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.FaceDownModeComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.PyrotechnicPerformer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pyrotechnic Performer — "Whenever this creature or another creature you control is turned face up,
 * that creature deals damage equal to its power to each opponent."
 *
 * Two things the wiring has to get right, and both are asserted here:
 *
 * - The clause has **no "another"**, so flipping the Performer itself must fire it. That is the
 *   `Player.You`-scoped `Triggers.CreatureTurnedFaceUp` (ANY binding), and the first test would fail
 *   with a SELF-excluding OTHER binding.
 * - **The amount comes from the flipped creature, not the Performer.** The second test flips a
 *   different, larger creature while the Performer sits face up on the battlefield, so a
 *   mis-bound `EntityReference.Source` would deal 3 instead of the other creature's power.
 */
class PyrotechnicPerformerScenarioTest : FunSpec({

    // A plain disguise creature whose power is unmistakably not the Performer's 3, so the second
    // test can tell "the flipped creature's power" apart from "the source's power".
    val behemoth = card("Disguised Behemoth") {
        manaCost = "{6}{G}"
        typeLine = "Creature — Beast"
        power = 8
        toughness = 8
        disguise = "{G}"
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PyrotechnicPerformer)
        driver.registerCard(behemoth)
        return driver
    }

    /** Put [cardName] onto the battlefield face down under disguise, as a real face-down entry would. */
    fun GameTestDriver.putDisguised(playerId: EntityId, cardName: String): EntityId {
        val id = putCreatureOnBattlefield(playerId, cardName)
        val cardDef = cardRegistry.requireCard(cardName)
        replaceState(
            state.updateEntity(id) { container ->
                var c = container.with(FaceDownComponent).with(FaceDownModeComponent(FaceDownMode.DISGUISE))
                FaceDownTurnUp.dataFor(cardDef, cardName, FaceDownMode.DISGUISE)?.let { c = c.with(it) }
                c
            }
        )
        removeSummoningSickness(id)
        return id
    }

    test("flipping the Performer itself burns each opponent for its own power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val performer = driver.putDisguised(you, "Pyrotechnic Performer")
        driver.giveMana(you, Color.RED, 1) // Disguise {R}

        driver.submit(
            TurnFaceUp(playerId = you, sourceId = performer, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null

        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()

        withClue("the trigger has no \"another\" clause, so the Performer's own flip fires it for 3") {
            driver.getLifeTotal(opp) shouldBe 17
        }
        withClue("its controller is untouched — the damage goes to each opponent only") {
            driver.getLifeTotal(you) shouldBe 20
        }
    }

    test("the damage is the flipped creature's power, not the Performer's") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // The Performer is already face up and watching.
        driver.putCreatureOnBattlefield(you, "Pyrotechnic Performer")
        val giant = driver.putDisguised(you, "Disguised Behemoth")
        driver.giveMana(you, Color.GREEN, 1) // Disguise {G}

        driver.submit(
            TurnFaceUp(playerId = you, sourceId = giant, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null

        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()

        withClue("8/8 flipped up → 8 damage to each opponent, not the Performer's 3") {
            driver.getLifeTotal(opp) shouldBe 12
        }
    }
})
