package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Web-Shooters (SPM) — {1}{W} Artifact — Equipment.
 *
 *  "Equipped creature gets +1/+1 and has reach and 'Whenever this creature attacks,
 *   tap target creature an opponent controls.' Equip {2}"
 *
 * Verifies the equipped creature gains +1/+1 and reach, and that attacking taps an opponent's
 * creature via the granted attack trigger.
 */
class WebShootersScenarioTest : FunSpec({

    val projector = StateProjector()

    val equipId = TestCards.all.first { it.name == "Web-Shooters" }
        .activatedAbilities.first().id

    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    // Advance to player 1's precombat main so equip (sorcery-speed) is legal for player 1.
    fun GameTestDriver.advanceToPlayer1PrecombatMain() {
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.PRECOMBAT_MAIN)
            safety++
        }
    }

    test("equipping grants +1/+1 and reach; attacking taps an opponent's creature") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val p1 = driver.player1
        val p2 = driver.player2

        // Bear = 2/2 with no reach. Equipment under p1's control. Opponent has a creature to tap.
        val bear = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.removeSummoningSickness(bear)
        val shooters = driver.putPermanentOnBattlefield(p1, "Web-Shooters")
        val victim = driver.putCreatureOnBattlefield(p2, "Grizzly Bears")

        // Baseline: bear is a plain 2/2 with no reach.
        projector.project(driver.state).getPower(bear) shouldBe 2
        projector.project(driver.state).getToughness(bear) shouldBe 2
        projector.project(driver.state).hasKeyword(bear, Keyword.REACH) shouldBe false

        // Equip {2} at sorcery speed on player 1's main phase.
        driver.advanceToPlayer1PrecombatMain()
        driver.giveColorlessMana(p1, 2)
        driver.submitSuccess(
            ActivateAbility(p1, shooters, equipId, targets = listOf(ChosenTarget.Permanent(bear)))
        )
        driver.bothPass()

        driver.state.getEntity(shooters)?.get<AttachedToComponent>()?.targetId shouldBe bear

        val equipped = projector.project(driver.state)
        equipped.getPower(bear) shouldBe 3
        equipped.getToughness(bear) shouldBe 3
        equipped.hasKeyword(bear, Keyword.REACH) shouldBe true

        // Attack with the equipped bear; the granted attack trigger fires and pauses for a target.
        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(p1, listOf(bear), p2)

        driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        val decision = driver.pendingDecision as ChooseTargetsDecision
        decision.legalTargets.values.flatten().toSet().contains(victim) shouldBe true

        driver.submitTargetSelection(p1, listOf(victim))
        driver.bothPass()

        // The opponent's creature is now tapped.
        (driver.state.getEntity(victim)?.get<TappedComponent>() != null) shouldBe true
    }
})
