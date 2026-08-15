package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.UnexpectedRequest
import com.wingedsheep.mtg.sets.definitions.mrd.cards.Bonesplitter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Unexpected Request — {2}{R} Sorcery (FIN).
 *
 * Gain control of target creature until end of turn. Untap that creature. It gains haste until end
 * of turn. You may attach an Equipment you control to that creature. If you do, unattach it at the
 * beginning of the next end step.
 *
 * The until-end-of-turn control change is a continuous effect, so control is read from the
 * projected state (cf. StolenUniformScenarioTest), not the base [getController].
 */
class UnexpectedRequestScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(UnexpectedRequest, Bonesplitter))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("gains control of the target creature until end of turn and grants it haste") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        driver.giveMana(me, Color.RED, 3)
        val spell = driver.putCardInHand(me, "Unexpected Request")
        driver.castSpell(me, spell, targets = listOf(courser)).error shouldBe null
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getController(courser) shouldBe me
        projected.hasKeyword(courser, Keyword.HASTE) shouldBe true
    }

    test("optionally attaches a chosen Equipment to the borrowed creature") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")
        val bonesplitter = driver.putPermanentOnBattlefield(me, "Bonesplitter")

        driver.giveMana(me, Color.RED, 3)
        val spell = driver.putCardInHand(me, "Unexpected Request")
        driver.castSpellWithTargets(
            me, spell,
            listOf(ChosenTarget.Permanent(courser), ChosenTarget.Permanent(bonesplitter))
        ).error shouldBe null
        driver.bothPass()

        projector.project(driver.state).getController(courser) shouldBe me
        driver.state.getEntity(bonesplitter)?.get<AttachedToComponent>()?.targetId shouldBe courser
    }

    test("declining the Equipment attaches nothing but still steals the creature") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")
        val bonesplitter = driver.putPermanentOnBattlefield(me, "Bonesplitter")

        driver.giveMana(me, Color.RED, 3)
        val spell = driver.putCardInHand(me, "Unexpected Request")
        // Provide only the creature target; the optional Equipment slot is left empty.
        driver.castSpell(me, spell, targets = listOf(courser)).error shouldBe null
        driver.bothPass()

        projector.project(driver.state).getController(courser) shouldBe me
        driver.state.getEntity(bonesplitter)?.get<AttachedToComponent>() shouldBe null
    }
})
