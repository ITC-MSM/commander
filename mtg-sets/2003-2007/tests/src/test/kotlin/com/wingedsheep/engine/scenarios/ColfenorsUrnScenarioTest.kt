package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.ColfenorsUrn
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Colfenor's Urn (LRW #254).
 *
 * "Whenever a creature with toughness 4 or greater is put into your graveyard from the
 *  battlefield, you may exile it.
 *  At the beginning of the end step, if three or more cards have been exiled with this artifact,
 *  sacrifice it. If you do, return those cards to the battlefield under their owner's control."
 *
 * Two axes are worth pinning, because both fail silently:
 *
 * - **The toughness gate is last-known information.** The creature is in the graveyard by the
 *   time the trigger is checked, so a matcher reading its live characteristics would find no
 *   toughness at all and either fire on everything or on nothing. A 5/5 must trigger and a 3/3
 *   must not.
 * - **The end-step ability's intervening "if" counts the linked pile, not the turn.** Below three
 *   cards nothing happens and the Urn survives; at three it sacrifices itself and returns all
 *   three. Testing only the "at three" half would pass against an ungated ability that fires
 *   every end step.
 */
class ColfenorsUrnScenarioTest : FunSpec({

    fun resolveStack(driver: GameTestDriver) {
        while (driver.stackSize > 0 && driver.state.pendingDecision == null) {
            driver.bothPass()
        }
    }

    fun setUp(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ColfenorsUrn))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(player, "Colfenor's Urn")
        return driver to player
    }

    /**
     * Put [cardName] onto the battlefield and Doom Blade it, answering the Urn's "may" with [exile].
     *
     * Passes only while something is actually on the stack: `bothPass()` on an *empty* stack
     * advances the step, which would walk the game out of the main phase before the next
     * (sorcery-speed) Doom Blade and leave the later kills silently uncast.
     */
    fun killCreature(driver: GameTestDriver, player: EntityId, cardName: String, exile: Boolean) {
        driver.putCreatureOnBattlefield(player, cardName)
        val target = driver.findPermanent(player, cardName)!!
        val blade = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 2)
        driver.castSpellWithTargets(player, blade, listOf(ChosenTarget.Permanent(target)))
        resolveStack(driver)
        if (driver.state.pendingDecision is YesNoDecision) {
            driver.submitYesNo(player, exile)
            resolveStack(driver)
        }
    }


    test("a toughness-5 creature offers the exile; a toughness-3 creature does not") {
        val (driver, player) = setUp()

        // Centaur Courser is 3/3 — below the gate, so no trigger and the card stays in the yard.
        killCreature(driver, player, "Centaur Courser", exile = true)
        driver.getGraveyardCardNames(player) shouldContain "Centaur Courser"
        driver.getExileCardNames(player).contains("Centaur Courser") shouldBe false

        // Force of Nature is 5/5 — the trigger fires and the exile is taken.
        killCreature(driver, player, "Force of Nature", exile = true)
        driver.getExileCardNames(player) shouldContain "Force of Nature"
    }

    test("declining the may leaves the creature in the graveyard") {
        val (driver, player) = setUp()

        killCreature(driver, player, "Force of Nature", exile = false)

        driver.getGraveyardCardNames(player) shouldContain "Force of Nature"
        driver.getExileCardNames(player).contains("Force of Nature") shouldBe false
    }

    test("two exiled cards is below the threshold — the Urn survives the end step") {
        val (driver, player) = setUp()

        killCreature(driver, player, "Force of Nature", exile = true)
        killCreature(driver, player, "Force of Nature", exile = true)
        driver.getExileCardNames(player).count { it == "Force of Nature" } shouldBe 2

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.findPermanent(player, "Colfenor's Urn").shouldNotBeNull()
        driver.getExileCardNames(player).count { it == "Force of Nature" } shouldBe 2
    }

    test("three exiled cards sacrifices the Urn and returns them to the battlefield") {
        val (driver, player) = setUp()

        repeat(3) { killCreature(driver, player, "Force of Nature", exile = true) }
        driver.getExileCardNames(player).count { it == "Force of Nature" } shouldBe 3

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.findPermanent(player, "Colfenor's Urn") shouldBe null
        driver.getCreatures(player).count { driver.getCardName(it) == "Force of Nature" } shouldBe 3
    }
})
