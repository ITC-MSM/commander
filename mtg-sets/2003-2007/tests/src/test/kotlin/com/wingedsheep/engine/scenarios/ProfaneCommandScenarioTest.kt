package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.ProfaneCommand
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Profane Command (LRW #135) — {X}{B}{B} Sorcery.
 *
 * Choose two —
 * • Target player loses X life.
 * • Return target creature card with mana value X or less from your graveyard to the battlefield.
 * • Target creature gets -X/-X until end of turn.
 * • Up to X target creatures gain fear until end of turn.
 *
 * The single X paid at cast time has to reach three grammatically different places — an *amount*,
 * a *filter bound*, and a *target count* — and each is a separate SDK spelling that can fail on its
 * own without changing how the card reads. So each is aimed at the boundary the paid X draws:
 *
 *  - the reanimation filter, against a creature card sitting exactly on X and then against the same
 *    card with X one lower, because `manaValueAtMostX` reading the wrong number is invisible;
 *  - the fear count, by asking for X+1 targets and expecting the cast to be refused, because a
 *    `dynamicMaxCount` that never resolved would accept any number of them;
 *  - the -X/-X, which negates through `Multiply(XValue, -1)` — a sign error there reads as a pump.
 *
 * The 2007-10-01 ruling ("the value chosen for X applies to each X in the spell's effect. You pay
 * {X} only once") is the first test: both chosen modes see the *same* X off one payment.
 */
class ProfaneCommandScenarioTest : FunSpec({

    val projector = StateProjector()

    // Mode order follows the printed bullets.
    val loseLife = 0
    val reanimate = 1
    val weaken = 2
    val grantFear = 3

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ProfaneCommand))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castCommand(
        caster: EntityId,
        x: Int,
        modes: List<Int>,
        modeTargets: List<List<ChosenTarget>>,
    ): ExecutionResult {
        giveMana(caster, Color.BLACK, x + 2)
        val spell = putCardInHand(caster, "Profane Command")
        return submit(
            CastSpell(
                playerId = caster,
                cardId = spell,
                targets = modeTargets.flatten(),
                chosenModes = modes,
                modeTargetsOrdered = modeTargets,
                xValue = x,
            )
        )
    }

    test("one payment, one X — 4 life lost and -4/-4 come off the same cast") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val bear = driver.putCreatureOnBattlefield(opp, "Grizzly Bears") // 2/2

        driver.castCommand(
            me,
            x = 4,
            modes = listOf(loseLife, weaken),
            modeTargets = listOf(
                listOf(ChosenTarget.Player(opp)),
                listOf(ChosenTarget.Permanent(bear)),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.getLifeTotal(opp) shouldBe 16
        // -4/-4 on a 2/2 is lethal, so the creature is gone rather than sitting at -2/-2.
        driver.findPermanent(opp, "Grizzly Bears") shouldBe null
    }

    test("-X/-X shrinks — a creature that survives it sits at reduced stats, not raised ones") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val nature = driver.putCreatureOnBattlefield(opp, "Force of Nature") // 5/5

        driver.castCommand(
            me,
            x = 3,
            modes = listOf(weaken, loseLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(nature)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldBe null
        driver.bothPass()

        projector.getProjectedPower(driver.state, nature) shouldBe 2
        projector.getProjectedToughness(driver.state, nature) shouldBe 2
    }

    test("the reanimation filter reads the paid X — a card sitting exactly on X comes back") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCardInGraveyard(me, "Centaur Courser") // mana value 3

        driver.castCommand(
            me,
            x = 3,
            modes = listOf(reanimate, loseLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Card(courser, me, Zone.GRAVEYARD)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.getCreatures(me).map { driver.getCardName(it) } shouldBe listOf("Centaur Courser")
    }

    test("the same card one point above the paid X is not a legal target") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCardInGraveyard(me, "Centaur Courser") // mana value 3

        driver.castCommand(
            me,
            x = 2,
            modes = listOf(reanimate, loseLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Card(courser, me, Zone.GRAVEYARD)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldNotBe null

        driver.getGraveyardCardNames(me) shouldBe listOf("Centaur Courser")
    }

    test("'up to X target creatures' takes X of them and grants fear to each") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val lions = driver.putCreatureOnBattlefield(me, "Savannah Lions")

        driver.castCommand(
            me,
            x = 2,
            modes = listOf(grantFear, loseLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(bears), ChosenTarget.Permanent(lions)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldBe null
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.hasKeyword(bears, Keyword.FEAR) shouldBe true
        projected.hasKeyword(lions, Keyword.FEAR) shouldBe true
    }

    // The web client's choose-N modal panel submits the modes (and the announced X) and lets the
    // server drive per-mode targeting on the battlefield — no flat `targets`, no
    // `modeTargetsOrdered`. That path prices its own target prompts, so the X announced with the
    // modes has to reach them too, or the same three spellings that the direct-cast tests above
    // cover go back to reading an unbound X.
    fun GameTestDriver.castCommandClientShaped(
        caster: EntityId,
        x: Int,
        modes: List<Int>,
    ): ExecutionResult {
        giveMana(caster, Color.BLACK, x + 2)
        val spell = putCardInHand(caster, "Profane Command")
        return submit(CastSpell(playerId = caster, cardId = spell, chosenModes = modes, xValue = x))
    }

    test("server-driven mode targeting offers X creatures for the fear mode, not one") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val lions = driver.putCreatureOnBattlefield(me, "Savannah Lions")

        driver.castCommandClientShaped(me, x = 2, modes = listOf(grantFear, loseLife))
            .error shouldBe null

        val fearDecision = driver.pendingDecision as ChooseTargetsDecision
        // "Up to X target creatures" — the static count on the requirement is the placeholder 1.
        fearDecision.targetRequirements.single().maxTargets shouldBe 2
        driver.submitTargetSelection(me, listOf(bears, lions))

        val playerDecision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(me, listOf(opp))
        playerDecision.targetRequirements.single().maxTargets shouldBe 1

        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.hasKeyword(bears, Keyword.FEAR) shouldBe true
        projected.hasKeyword(lions, Keyword.FEAR) shouldBe true
        driver.getLifeTotal(opp) shouldBe 18
    }

    test("server-driven mode targeting hides a graveyard creature above the announced X") {
        val driver = newDriver()
        val me = driver.activePlayer!!

        val courser = driver.putCardInGraveyard(me, "Centaur Courser") // mana value 3
        val bears = driver.putCardInGraveyard(me, "Grizzly Bears") // mana value 2

        driver.castCommandClientShaped(me, x = 2, modes = listOf(reanimate, loseLife))
            .error shouldBe null

        val reanimateDecision = driver.pendingDecision as ChooseTargetsDecision
        reanimateDecision.legalTargets[0] shouldBe listOf(bears)
        reanimateDecision.legalTargets[0]!!.contains(courser) shouldBe false
    }

    test("'up to X target creatures' refuses X + 1 of them") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val lions = driver.putCreatureOnBattlefield(me, "Savannah Lions")

        driver.castCommand(
            me,
            x = 1,
            modes = listOf(grantFear, loseLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(bears), ChosenTarget.Permanent(lions)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldNotBe null
    }
})
