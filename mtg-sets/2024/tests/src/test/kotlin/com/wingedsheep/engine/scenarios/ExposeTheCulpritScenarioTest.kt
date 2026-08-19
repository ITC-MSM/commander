package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.effects.FaceDownTurnUp
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.FaceDownModeComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.BasilicaStalker
import com.wingedsheep.mtg.sets.definitions.mkm.cards.ExposeTheCulprit
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Expose the Culprit — "Choose one or both — • Turn target face-down creature face up. • Exile any
 * number of face-up creatures you control with disguise in a face-down pile, shuffle that pile,
 * then cloak them."
 *
 * The card's own logic is two existing primitives, so what actually needs proving here is the new
 * `withDisguise()` filter and the two shapes it has to get right:
 *
 *  1. it selects on the **printed** disguise ability, so a vanilla creature is never offered even
 *     though it is just as face-up and just as yours — a filter that fell through to "any creature
 *     you control" would still pass a happy-path test;
 *  2. it reads the card, not the turn-up procedure, so it holds for a creature that is *face up*
 *     right now — which is the only state the mode can ever see.
 *
 * Plus the mode-2 outcome itself: the chosen creature really leaves via exile and comes back
 * cloaked (face down, `FaceDownMode.CLOAK`), which is what makes it a new object rather than a
 * turned-over one.
 */
class ExposeTheCulpritScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ExposeTheCulprit)
        driver.registerCard(BasilicaStalker)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Put [cardName] onto [playerId]'s battlefield face down under [mode], the way a real entry does. */
    fun GameTestDriver.putFaceDown(playerId: EntityId, cardName: String, mode: FaceDownMode): EntityId {
        val id = putPermanentOnBattlefield(playerId, cardName)
        val cardDef = cardRegistry.requireCard(cardName)
        replaceState(
            state.updateEntity(id) { container ->
                var c = container.with(FaceDownComponent).with(FaceDownModeComponent(mode))
                FaceDownTurnUp.dataFor(cardDef, cardName, mode)?.let { c = c.with(it) }
                c
            }
        )
        return id
    }

    fun GameTestDriver.isFaceDown(id: EntityId): Boolean =
        state.getEntity(id)?.has<FaceDownComponent>() == true

    /** Cast the spell with exactly [modes] chosen, each carrying the targets in [targetsPerMode]. */
    fun GameTestDriver.castExpose(modes: List<Int>, targetsPerMode: List<List<ChosenTarget>>) {
        val spell = putCardInHand(player1, "Expose the Culprit")
        giveMana(player1, Color.RED, 4)
        submit(
            CastSpell(
                playerId = player1,
                cardId = spell,
                targets = targetsPerMode.flatten(),
                chosenModes = modes,
                modeTargetsOrdered = targetsPerMode
            )
        ).error shouldBe null
        bothPass()
    }

    test("mode 1 turns a face-down creature face up") {
        val driver = newDriver()
        val hidden = driver.putFaceDown(driver.player1, "Basilica Stalker", FaceDownMode.DISGUISE)
        driver.isFaceDown(hidden) shouldBe true

        driver.castExpose(listOf(0), listOf(listOf(ChosenTarget.Permanent(hidden))))

        withClue("no cost is paid — the effect turns it face up outright") {
            driver.isFaceDown(hidden) shouldBe false
        }
        val projected = StateProjector().project(driver.state)
        projected.getPower(hidden) shouldBe 3
        projected.getToughness(hidden) shouldBe 4
    }

    test("mode 2 offers only face-up creatures you control with disguise") {
        val driver = newDriver()
        val stalker = driver.putCreatureOnBattlefield(driver.player1, "Basilica Stalker")
        val bears = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val theirStalker = driver.putCreatureOnBattlefield(driver.player2, "Basilica Stalker")
        val hidden = driver.putFaceDown(driver.player1, "Basilica Stalker", FaceDownMode.DISGUISE)

        driver.castExpose(listOf(1), listOf(emptyList()))

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        val offered = decision.options.toSet()
        withClue("a face-up disguise creature you control is the only legal pick") {
            offered shouldBe setOf(stalker)
        }
        withClue("no disguise on the card") { offered.contains(bears) shouldBe false }
        withClue("not yours") { offered.contains(theirStalker) shouldBe false }
        withClue("already face down, so not face-up") { offered.contains(hidden) shouldBe false }
    }

    test("mode 2 exiles the chosen creature and returns it cloaked") {
        val driver = newDriver()
        val stalker = driver.putCreatureOnBattlefield(driver.player1, "Basilica Stalker")

        driver.castExpose(listOf(1), listOf(emptyList()))
        driver.submitCardSelection(driver.player1, listOf(stalker))

        withClue("it is back on the battlefield, not left in exile") {
            driver.state.getZone(driver.player1, Zone.BATTLEFIELD).contains(stalker) shouldBe true
        }
        driver.isFaceDown(stalker) shouldBe true
        driver.state.getEntity(stalker)?.get<FaceDownModeComponent>()?.mode shouldBe FaceDownMode.CLOAK

        val projected = StateProjector().project(driver.state)
        withClue("a cloaked permanent is a 2/2 (CR 701.58a), not the 3/4 it was") {
            projected.getPower(stalker) shouldBe 2
            projected.getToughness(stalker) shouldBe 2
        }
    }

    test("mode 2 selecting nothing is legal and changes nothing") {
        val driver = newDriver()
        val stalker = driver.putCreatureOnBattlefield(driver.player1, "Basilica Stalker")

        driver.castExpose(listOf(1), listOf(emptyList()))
        driver.submitCardSelection(driver.player1, emptyList())

        driver.isFaceDown(stalker) shouldBe false
        StateProjector().project(driver.state).getPower(stalker) shouldBe 3
    }

    test("both modes — one creature flips up while another is cloaked") {
        val driver = newDriver()
        val hidden = driver.putFaceDown(driver.player1, "Basilica Stalker", FaceDownMode.DISGUISE)
        val stalker = driver.putCreatureOnBattlefield(driver.player1, "Basilica Stalker")

        driver.castExpose(listOf(0, 1), listOf(listOf(ChosenTarget.Permanent(hidden)), emptyList()))
        driver.submitCardSelection(driver.player1, listOf(stalker))

        driver.isFaceDown(hidden) shouldBe false
        driver.isFaceDown(stalker) shouldBe true
    }
})
