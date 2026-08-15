package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.AvatarTheLastAirbenderSet
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression: "any target" must read the PROJECTED type line, not the printed one.
 *
 * An Earthbend land (e.g. via Earthbending Lesson) becomes a 0/0 land *creature*
 * via an [AnimateLandEffect] floating layer — the CREATURE type lives in the
 * projected state, not on the card's printed type line, which still says "Land".
 *
 * Cat-Gator's ETB ("deals damage … to any target") uses [AnyTarget]. The bug:
 * [TargetFinder.findAnyTargets] filtered candidates with the printed
 * `cardComponent.typeLine.isCreature`, so an animated land was never offered as a
 * legal target — Cat-Gator could not deal damage to an earthbended land.
 */
class CatGatorEarthbendTargetingTest : FunSpec({

    val targetFinder = TargetFinder()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + AvatarTheLastAirbenderSet.cards)
        return driver
    }

    test("an earthbended land is a legal \"any target\"") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putLandOnBattlefield(you, "Forest")

        // Before animation: a plain Forest is NOT a legal "any target",
        // nor a legal "target creature or planeswalker".
        targetFinder.findLegalTargets(driver.state, AnyTarget(), controllerId = you)
            .contains(forest) shouldBe false
        targetFinder.findLegalTargets(driver.state, TargetCreatureOrPlaneswalker(), controllerId = you)
            .contains(forest) shouldBe false

        // Earthbend the Forest into a creature-land.
        val lesson = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        // After animation: the Forest is a creature in projection, so both "any target"
        // (Cat-Gator's ETB) and "target creature or planeswalker" can now legally target it.
        targetFinder.findLegalTargets(driver.state, AnyTarget(), controllerId = you)
            .contains(forest) shouldBe true
        targetFinder.findLegalTargets(driver.state, TargetCreatureOrPlaneswalker(), controllerId = you)
            .contains(forest) shouldBe true
    }
})
