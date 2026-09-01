package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.WoodwraithCorrupter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Woodwraith Corrupter (RAV #240) — "{1}{B}{G}, {T}: Target Forest becomes a 4/4 black and green
 * Elemental Horror creature. It's still a land."
 *
 * The claim worth pinning is the one the card's own ruling makes: the animation has **no stated
 * duration**, so it survives the cleanup step. A `Duration.EndOfTurn` slip reads identically on the
 * turn it is used and is invisible to the snapshot net, so the "still a 4/4 next turn" assertion is
 * the whole point of this file. Beside it: the Forest keeps LAND and its Forest subtype ("it's
 * still a land", so it still taps for {G}), and its colors are *replaced* with black and green
 * rather than added to — an animated Forest is not green-only.
 */
class WoodwraithCorrupterScenarioTest : FunSpec({

    val animateAbility = WoodwraithCorrupter.activatedAbilities.single().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + WoodwraithCorrupter)
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 20,
            startingPlayer = 0,
            skipMulligans = true,
        )
        return driver
    }

    fun GameTestDriver.drainStack() {
        var guard = 0
        while (stackSize > 0 && guard++ < 50) bothPass()
    }

    /**
     * A Corrupter that has been under [me]'s control since the turn began, plus a Forest, with the
     * animation already paid for and resolved against that Forest.
     */
    fun animate(driver: GameTestDriver, me: EntityId, forestController: EntityId): EntityId {
        val corrupter = driver.putPermanentOnBattlefield(me, "Woodwraith Corrupter")
        driver.removeSummoningSickness(corrupter)
        val forest = driver.putLandOnBattlefield(forestController, "Forest")

        driver.giveColorlessMana(me, 1)
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveMana(me, Color.GREEN, 1)
        driver.submit(
            ActivateAbility(
                playerId = me,
                sourceId = corrupter,
                abilityId = animateAbility,
                targets = listOf(ChosenTarget.Permanent(forest)),
            )
        ).isSuccess shouldBe true
        driver.drainStack()
        return forest
    }

    test("the targeted Forest becomes a 4/4 black and green Elemental Horror that is still a land") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val forest = animate(driver, me, me)
        val projected = driver.state.projectedState

        projected.isCreature(forest) shouldBe true
        projected.getPower(forest) shouldBe 4
        projected.getToughness(forest) shouldBe 4
        withClue("both printed subtypes are granted") {
            projected.hasSubtype(forest, "Elemental") shouldBe true
            projected.hasSubtype(forest, "Horror") shouldBe true
        }

        withClue("\"It's still a land\" — LAND and the Forest subtype survive, so it still taps for {G}") {
            projected.hasType(forest, "LAND") shouldBe true
            projected.hasSubtype(forest, "Forest") shouldBe true
        }
        withClue("becomes black *and green*, replacing the land's colorlessness") {
            projected.getColors(forest) shouldBe setOf(Color.BLACK.name, Color.GREEN.name)
        }
    }

    test("the animation has no duration, so it survives into the next turn") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val forest = animate(driver, me, me)
        val turnAnimated = driver.state.turnNumber

        driver.passPriorityUntil(Step.UPKEEP)
        driver.drainStack()
        withClue("the test must actually have crossed a turn boundary") {
            (driver.state.turnNumber != turnAnimated || driver.activePlayer != me) shouldBe true
        }

        val projected = driver.state.projectedState
        withClue("a Duration.EndOfTurn slip is invisible until here") {
            projected.isCreature(forest) shouldBe true
            projected.getPower(forest) shouldBe 4
            projected.getToughness(forest) shouldBe 4
        }
    }

    test("an opponent's Forest is a legal target") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val forest = animate(driver, me, opponent)

        driver.state.projectedState.isCreature(forest) shouldBe true
        withClue("animating it does not steal it") {
            driver.state.projectedState.getController(forest) shouldBe opponent
        }
    }
})
