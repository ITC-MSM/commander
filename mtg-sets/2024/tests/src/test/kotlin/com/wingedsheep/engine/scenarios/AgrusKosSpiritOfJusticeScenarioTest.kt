package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.AgrusKosSpiritOfJustice
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Agrus Kos, Spirit of Justice (MKM #184) — {2}{R}{W} 2/4 Legendary Spirit Detective.
 *
 * "Double strike, vigilance. Whenever Agrus Kos enters or attacks, choose up to one target creature.
 *  If it's suspected, exile it. Otherwise, suspect it."
 *
 * Three claims worth proving, because each has a plausible wrong implementation:
 *
 *  - the branch is a **resolution-time** state test, so a clean creature takes the suspect arm and a
 *    creature that is already suspected takes the exile arm;
 *  - "enters **or** attacks" is genuinely two abilities — the attacks half must fire on its own, not
 *    only as a side effect of the entry trigger;
 *  - "up to one target" must let the ability resolve with nothing chosen, rather than being removed
 *    from the stack for want of a legal target.
 *
 * The two-stage test doubles as the card's real gameplay loop: entry suspects the blocker, the next
 * attack cashes the designation in for an exile.
 */
class AgrusKosSpiritOfJusticeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + AgrusKosSpiritOfJustice)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.payForAgrusKos(player: EntityId) {
        giveMana(player, Color.RED, 1)
        giveMana(player, Color.WHITE, 1)
        giveColorlessMana(player, 2)
    }

    /** Drain priority until the trigger's target choice is raised, however many passes that takes. */
    fun GameTestDriver.advanceToTargetChoice() {
        var guard = 0
        while (guard++ < 12 && pendingDecision !is ChooseTargetsDecision) {
            if (stackSize > 0 || priorityPlayer != null) bothPass() else break
        }
        withClue("the trigger asked for its 'up to one target creature'") {
            (pendingDecision is ChooseTargetsDecision) shouldBe true
        }
    }

    test("the enters trigger suspects a target that isn't suspected yet") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val victim = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        withClue("the victim starts clean") {
            StateProjector().project(driver.state).isSuspected(victim) shouldBe false
        }

        val card = driver.putCardInHand(player, "Agrus Kos, Spirit of Justice")
        driver.payForAgrusKos(player)
        driver.castSpell(player, card).error shouldBe null
        driver.advanceToTargetChoice()
        driver.submitTargetSelection(player, listOf(victim)).error shouldBe null
        driver.bothPass()

        val projected = StateProjector().project(driver.state)
        withClue("the else arm applied the suspected designation (CR 701.60a)") {
            projected.isSuspected(victim) shouldBe true
        }
        withClue("suspected carries menace and can't block (CR 701.60c)") {
            projected.hasKeyword(victim, Keyword.MENACE) shouldBe true
            projected.cantBlock(victim) shouldBe true
        }
        withClue("the exile arm must not have run — it wasn't suspected when the ability resolved") {
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe victim
        }
    }

    test("the attacks trigger exiles a target the enters trigger already suspected") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val victim = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Stage one: hard-cast Agrus Kos, whose entry trigger suspects the victim.
        val card = driver.putCardInHand(player, "Agrus Kos, Spirit of Justice")
        driver.payForAgrusKos(player)
        driver.castSpell(player, card).error shouldBe null
        driver.advanceToTargetChoice()
        driver.submitTargetSelection(player, listOf(victim)).error shouldBe null
        driver.bothPass()

        val agrus = driver.findPermanent(player, "Agrus Kos, Spirit of Justice")
        agrus.shouldNotBeNull()
        StateProjector().project(driver.state).isSuspected(victim) shouldBe true

        // Stage two: attack with it. This is the *second* ability, and the target is now suspected.
        driver.removeSummoningSickness(agrus)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(agrus), opponent).error shouldBe null
        driver.advanceToTargetChoice()
        driver.submitTargetSelection(player, listOf(victim)).error shouldBe null
        driver.bothPass()

        withClue("the attacks half fired on its own and took the exile arm") {
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
            driver.getExileCardNames(opponent) shouldContain "Grizzly Bears"
        }
    }

    test("'up to one target' lets the ability resolve with nothing chosen") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val victim = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val card = driver.putCardInHand(player, "Agrus Kos, Spirit of Justice")
        driver.payForAgrusKos(player)
        driver.castSpell(player, card).error shouldBe null
        driver.advanceToTargetChoice()

        // Decline the optional target even though a legal one is on the board.
        driver.submitTargetSelection(player, emptyList()).error shouldBe null
        driver.bothPass()

        val projected = StateProjector().project(driver.state)
        withClue("neither arm touched anything") {
            projected.isSuspected(victim) shouldBe false
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe victim
        }
        withClue("Agrus Kos itself still arrived, with both printed keywords") {
            val agrus = driver.findPermanent(player, "Agrus Kos, Spirit of Justice")
            agrus.shouldNotBeNull()
            projected.hasKeyword(agrus, Keyword.DOUBLE_STRIKE) shouldBe true
            projected.hasKeyword(agrus, Keyword.VIGILANCE) shouldBe true
        }
    }
})
