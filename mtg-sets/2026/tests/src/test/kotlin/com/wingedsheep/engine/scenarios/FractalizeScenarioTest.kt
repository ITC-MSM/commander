package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.mechanics.layers.imageOverrideFor
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.sos.cards.Fractalize
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Fractalize ({X}{U} instant):
 *   "Until end of turn, target creature becomes a green and blue Fractal with base power and
 *    toughness each equal to X plus 1. (It loses all other colors and creature types.)"
 *
 * Exercises the now-dynamic `BecomeCreatureEffect` base P/T (= X plus 1, evaluated once at
 * resolution) together with the type-set (→ Fractal, losing other creature types) and color-set
 * (→ green + blue, losing other colors) layers:
 *  - X = 2 → a 3/3 green-and-blue Fractal; the original creature type (Centaur Warrior) and color
 *    (green only) are replaced.
 *  - X = 0 → a 1/1 (X plus 1 = 1).
 */
class FractalizeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(Fractalize))
        return driver
    }

    fun startTurn(driver: GameTestDriver): EntityId {
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver.activePlayer!!
    }

    val projector = StateProjector()

    test("X=2: target becomes a 3/3 green and blue Fractal, losing other types and colors") {
        val driver = createDriver()
        val p = startTurn(driver)
        // Centaur Courser: {2}{G}, 3/3, Centaur Warrior, green.
        val courser = driver.putCreatureOnBattlefield(p, "Centaur Courser")

        val spell = driver.putCardInHand(p, "Fractalize")
        driver.giveMana(p, Color.BLUE, 1) // {U}
        driver.giveColorlessMana(p, 2)    // {X} with X = 2
        driver.castXSpell(p, spell, xValue = 2, targets = listOf(courser)).isSuccess shouldBe true
        driver.bothPass()

        // Base P/T each X + 1 = 3.
        projector.getProjectedPower(driver.state, courser) shouldBe 3
        projector.getProjectedToughness(driver.state, courser) shouldBe 3

        val projected = driver.state.projectedState
        // Becomes green AND blue, loses all other colors (was green-only — now exactly G + U).
        projected.hasColor(courser, Color.GREEN) shouldBe true
        projected.hasColor(courser, Color.BLUE) shouldBe true
        projected.getColors(courser).size shouldBe 2
        // Becomes a Fractal, loses all other creature types (was Centaur Warrior).
        projected.hasSubtype(courser, "Fractal") shouldBe true
        projected.hasSubtype(courser, "Centaur") shouldBe false
        projected.hasSubtype(courser, "Warrior") shouldBe false
        projected.isCreature(courser) shouldBe true
    }

    test("X=0: target becomes a 1/1 Fractal (X plus 1 = 1)") {
        val driver = createDriver()
        val p = startTurn(driver)
        val courser = driver.putCreatureOnBattlefield(p, "Centaur Courser")

        val spell = driver.putCardInHand(p, "Fractalize")
        driver.giveMana(p, Color.BLUE, 1)
        driver.castXSpell(p, spell, xValue = 0, targets = listOf(courser)).isSuccess shouldBe true
        driver.bothPass()

        projector.getProjectedPower(driver.state, courser) shouldBe 1
        projector.getProjectedToughness(driver.state, courser) shouldBe 1
        driver.state.projectedState.hasSubtype(courser, "Fractal") shouldBe true
    }

    test("animated creature renders the Fractal token art, reverting at end of turn") {
        val driver = createDriver()
        val p = startTurn(driver)
        val courser = driver.putCreatureOnBattlefield(p, "Centaur Courser")

        val spell = driver.putCardInHand(p, "Fractalize")
        driver.giveMana(p, Color.BLUE, 1)
        driver.giveColorlessMana(p, 2)
        driver.castXSpell(p, spell, xValue = 2, targets = listOf(courser)).isSuccess shouldBe true
        driver.bothPass()

        val fractalArt = "https://cards.scryfall.io/normal/front/8/b/8b5f1fdb-04df-4224-acb4-7819c37565f5.jpg?1775828306"

        // Engine source of truth: the display-only image override is in force while animated.
        driver.state.imageOverrideFor(courser) shouldBe fractalArt

        // It surfaces on the client DTO as the rendered image (overriding the creature's own art).
        val transformer = ClientStateTransformer(cardRegistry = driver.cardRegistry)
        transformer.transform(driver.state, viewingPlayerId = p).cards[courser]?.imageUri shouldBe fractalArt

        // Advancing into the next turn runs this turn's cleanup, which removes the EndOfTurn
        // override along with the rest of the animate — the creature reverts to its own art.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.state.imageOverrideFor(courser) shouldBe fractalArt   // still same turn
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)                // next turn, past cleanup
        driver.state.imageOverrideFor(courser) shouldBe null
        // DTO no longer carries the Fractal art — it falls back to the creature's own image.
        transformer.transform(driver.state, viewingPlayerId = p).cards[courser]?.imageUri shouldNotBe fractalArt
    }
})
