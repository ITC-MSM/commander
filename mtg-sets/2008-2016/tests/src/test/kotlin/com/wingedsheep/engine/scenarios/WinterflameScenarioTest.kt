package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.Winterflame
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Winterflame — {1}{U}{R} Instant (KTK).
 *
 * Choose one or both —
 * • Tap target creature.
 * • Winterflame deals 2 damage to target creature.
 *
 * "Choose one or both" is `chooseCount = 2, minChooseCount = 1` (CR 700.2), and these tests exist
 * because it was previously written as *three* modes — tap, damage, and a third "do both" — with
 * `chooseCount = 1`. That spelling produces the same board in the common cases and is a different
 * card: it offers a mode the card does not print, and it tells everything downstream that the
 * player chose one mode when they chose two.
 */
class WinterflameScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Winterflame))
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castWinterflame(
        caster: com.wingedsheep.sdk.model.EntityId,
        modes: List<Int>,
        modeTargets: List<List<ChosenTarget>>,
    ) {
        giveMana(caster, Color.BLUE, 2)
        giveMana(caster, Color.RED, 1)
        val spell = putCardInHand(caster, "Winterflame")
        submit(
            CastSpell(
                playerId = caster,
                cardId = spell,
                targets = modeTargets.flatten(),
                chosenModes = modes,
                modeTargetsOrdered = modeTargets,
            )
        ).error shouldBe null
        bothPass()
    }

    test("both modes — one creature is tapped and a different one takes 2 damage") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser") // 3/3, survives 2
        val lions = driver.putCreatureOnBattlefield(opp, "Savannah Lions")    // 1/1, dies to 2

        driver.castWinterflame(
            me,
            modes = listOf(0, 1),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(courser)),
                listOf(ChosenTarget.Permanent(lions)),
            ),
        )

        driver.isTapped(courser) shouldBe true
        driver.findPermanent(opp, "Savannah Lions") shouldBe null
    }

    test("one mode is enough — minChooseCount is 1, and the other mode does not happen") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        driver.castWinterflame(
            me,
            modes = listOf(1),
            modeTargets = listOf(listOf(ChosenTarget.Permanent(courser))),
        )

        driver.findPermanent(opp, "Centaur Courser") shouldBe courser
        driver.isTapped(courser) shouldBe false
    }

    // Both modes may name the same creature — nothing on the card forbids it, and the tap and the
    // damage are separate effects hitting one permanent.
    test("both modes may pick the same creature") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        driver.castWinterflame(
            me,
            modes = listOf(0, 1),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(courser)),
                listOf(ChosenTarget.Permanent(courser)),
            ),
        )

        driver.isTapped(courser) shouldBe true
        driver.findPermanent(opp, "Centaur Courser") shouldBe courser
    }

    // The card prints two bullets. A third mode would be a mode the player could choose and the
    // card never offered, which is what the old three-mode spelling did.
    test("the card offers exactly the two modes it prints") {
        val modal = Winterflame.script.spellEffect
            as com.wingedsheep.sdk.scripting.effects.ModalEffect

        modal.modes.size shouldBe 2
        modal.chooseCount shouldBe 2
        modal.minChooseCount shouldBe 1
    }
})
