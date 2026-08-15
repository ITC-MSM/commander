package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.DontMakeASound
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Don't Make a Sound — {1}{U} Instant
 * Counter target spell unless its controller pays {2}. If they do, surveil 2.
 */
class DontMakeASoundScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DontMakeASound)
        return driver
    }

    test("counters the spell when its controller declines to pay {2}") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.passPriority(me)
        driver.castSpell(opponent, bolt, listOf(me))
        val boltOnStack = driver.getTopOfStack()!!
        driver.passPriority(opponent)

        val spell = driver.putCardInHand(me, "Don't Make a Sound")
        driver.giveMana(me, Color.BLUE, 1)
        driver.giveColorlessMana(me, 1)
        driver.castSpellWithTargets(me, spell, listOf(ChosenTarget.Spell(boltOnStack))).isSuccess shouldBe true

        // Opponent could pay {2} but will decline.
        driver.giveColorlessMana(opponent, 2)
        driver.bothPass()
        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        (driver.pendingDecision as YesNoDecision).playerId shouldBe opponent

        driver.submitYesNo(opponent, false)
        driver.getGraveyardCardNames(opponent) shouldContain "Lightning Bolt"
    }

    test("when the controller pays {2}, the spell resolves and the caster surveils 2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.passPriority(me)
        driver.castSpell(opponent, bolt, listOf(me))
        val boltOnStack = driver.getTopOfStack()!!
        driver.passPriority(opponent)

        val spell = driver.putCardInHand(me, "Don't Make a Sound")
        driver.giveMana(me, Color.BLUE, 1)
        driver.giveColorlessMana(me, 1)
        driver.castSpellWithTargets(me, spell, listOf(ChosenTarget.Spell(boltOnStack))).isSuccess shouldBe true

        // Opponent pays {2}.
        driver.giveColorlessMana(opponent, 2)
        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(opponent, true)

        // Paying triggers the caster's surveil 2 → pauses for the keep/graveyard choice.
        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        (driver.pendingDecision as SelectCardsDecision).playerId shouldBe me

        // The bolt was NOT countered.
        driver.getGraveyardCardNames(opponent) shouldNotContain "Lightning Bolt"
    }
})
