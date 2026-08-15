package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Untimely Malfunction (DSK #161) — {1}{R} Instant.
 *
 * "Choose one —
 *  • Destroy target artifact.
 *  • Change the target of target spell or ability with a single target.
 *  • One or two target creatures can't block this turn."
 *
 * Pure composition: a "choose one" [ModalEffect]. Mode 1 is Destroy on an artifact target,
 * mode 2 reuses the change-target machinery (Effects.ChangeTarget + SpellOrAbilityWithSingleTarget,
 * see Return the Favor / Willbender), mode 3 is a 1-2 variable-count creature target with a
 * per-target Effects.CantBlock via ForEachTargetEffect (same shape as Amazing Acrobatics). No new
 * SDK surface.
 */
class UntimelyMalfunctionScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("mode 0 (destroy artifact): destroys target artifact") {
        val driver = newDriver()
        val me = driver.player1
        // "Artifact Creature" is a 2/2 artifact — a legal target for "destroy target artifact".
        val artifact = driver.putPermanentOnBattlefield(driver.player2, "Artifact Creature")

        val malfunction = driver.putCardInHand(me, "Untimely Malfunction")
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 1)
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = malfunction,
                targets = listOf(ChosenTarget.Permanent(artifact)),
                chosenModes = listOf(0),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(artifact))),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(driver.player2, "Artifact Creature") shouldBe null
    }

    test("mode 1 (change target): redirects a single-target spell") {
        val driver = newDriver()
        val me = driver.player1
        val opponent = driver.getOpponent(me)

        // Opponent aims Lightning Bolt at me.
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.passPriority(me)
        driver.castSpellWithTargets(opponent, bolt, listOf(ChosenTarget.Player(me)))
        val boltOnStack = driver.getTopOfStack()!!
        driver.passPriority(opponent)

        val malfunction = driver.putCardInHand(me, "Untimely Malfunction")
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 1)
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = malfunction,
                targets = listOf(ChosenTarget.Spell(boltOnStack)),
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Spell(boltOnStack))),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true

        // Resolve Untimely Malfunction -> pause to choose the bolt's new (single) target.
        driver.bothPass()
        driver.submitCardSelection(me, listOf(opponent))

        // Resolve the redirected bolt -> opponent takes 3, I take none.
        var guard = 0
        while (driver.stackSize > 0 && guard < 20) {
            driver.bothPass()
            guard++
        }
        driver.getLifeTotal(opponent) shouldBe 17
        driver.getLifeTotal(me) shouldBe 20
    }

    test("mode 2 (can't block): one target creature can't block") {
        val driver = newDriver()
        val me = driver.player1
        val opponent = driver.getOpponent(me)
        val blocker = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val malfunction = driver.putCardInHand(me, "Untimely Malfunction")
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 1)
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = malfunction,
                targets = listOf(ChosenTarget.Permanent(blocker)),
                chosenModes = listOf(2),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(blocker))),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.projectedState.cantBlock(blocker) shouldBe true
    }

    test("mode 2 (can't block): two target creatures can't block") {
        val driver = newDriver()
        val me = driver.player1
        val opponent = driver.getOpponent(me)
        val blockerA = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val blockerB = driver.putCreatureOnBattlefield(opponent, "Hill Giant")

        val malfunction = driver.putCardInHand(me, "Untimely Malfunction")
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 1)
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = malfunction,
                targets = listOf(ChosenTarget.Permanent(blockerA), ChosenTarget.Permanent(blockerB)),
                chosenModes = listOf(2),
                modeTargetsOrdered = listOf(
                    listOf(ChosenTarget.Permanent(blockerA), ChosenTarget.Permanent(blockerB))
                ),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.projectedState.cantBlock(blockerA) shouldBe true
        driver.state.projectedState.cantBlock(blockerB) shouldBe true
    }
})
