package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.CrypticCommand
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cryptic Command (LRW #56) — {1}{U}{U}{U} Instant.
 *
 * Choose two —
 * • Counter target spell.
 * • Return target permanent to its owner's hand.
 * • Tap all creatures your opponents control.
 * • Draw a card.
 *
 * Two axes are worth pinning. The sweep is **one-sided** — "creatures your opponents control", not
 * every creature — and a controller predicate that fell back to the implicit "you control" would
 * look identical on the card while tapping exactly the wrong board, so the test puts creatures on
 * both sides and checks both. And the counter mode has to work from the opponent's turn, since a
 * sorcery-speed reading of an instant only shows up when there is a spell to answer.
 */
class CrypticCommandScenarioTest : FunSpec({

    // Mode order follows the printed bullets.
    val counterSpell = 0
    val bounce = 1
    val tapOpponents = 2
    val drawCard = 3

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CrypticCommand))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castCommand(
        caster: EntityId,
        modes: List<Int>,
        modeTargets: List<List<ChosenTarget>>,
    ): ExecutionResult {
        giveMana(caster, Color.BLUE, 4)
        val spell = putCardInHand(caster, "Cryptic Command")
        return submit(
            CastSpell(
                playerId = caster,
                cardId = spell,
                targets = modeTargets.flatten(),
                chosenModes = modes,
                modeTargetsOrdered = modeTargets,
            )
        )
    }

    test("the sweep taps only the opponents' creatures, never the caster's") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val mine = driver.putCreatureOnBattlefield(me, "Savannah Lions")
        val theirs = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val alsoTheirs = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        driver.castCommand(
            me,
            modes = listOf(tapOpponents, drawCard),
            modeTargets = listOf(emptyList(), emptyList()),
        ).error shouldBe null
        driver.bothPass()

        driver.isTapped(theirs) shouldBe true
        driver.isTapped(alsoTheirs) shouldBe true
        driver.isTapped(mine) shouldBe false
    }

    test("bounce and draw — the permanent goes to its owner's hand and a card is drawn") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val bears = driver.findPermanent(opp, "Grizzly Bears")!!
        val handBefore = driver.getHandSize(me)
        val oppHandBefore = driver.getHandSize(opp)

        driver.castCommand(
            me,
            modes = listOf(bounce, drawCard),
            modeTargets = listOf(listOf(ChosenTarget.Permanent(bears)), emptyList()),
        ).error shouldBe null
        driver.bothPass()

        driver.findPermanent(opp, "Grizzly Bears") shouldBe null
        // "its owner's hand" — the opponent's, not the caster's.
        driver.getHandSize(opp) shouldBe oppHandBefore + 1
        // The Command was added to the caster's hand and left it on the cast, so only the draw
        // shows up against the pre-cast count.
        driver.getHandSize(me) shouldBe handBefore + 1
    }

    test("counter and draw — the countered spell never resolves") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val target = driver.putCreatureOnBattlefield(me, "Centaur Courser")

        // The opponent casts a removal spell; the caster answers at instant speed.
        driver.giveMana(opp, Color.RED, 1)
        val bolt = driver.putCardInHand(opp, "Lightning Bolt")
        driver.passPriority(me)
        driver.submit(
            CastSpell(playerId = opp, cardId = bolt, targets = listOf(ChosenTarget.Permanent(target)))
        ).error shouldBe null

        // The caster retains priority after casting, so the opponent has to release it before the
        // Command can be cast in response.
        driver.passPriority(opp)

        val stackSpell = driver.getTopOfStack()!!
        driver.castCommand(
            me,
            modes = listOf(counterSpell, drawCard),
            modeTargets = listOf(listOf(ChosenTarget.Spell(stackSpell)), emptyList()),
        ).error shouldBe null
        driver.bothPass()
        driver.bothPass()

        driver.assertInGraveyard(opp, "Lightning Bolt")
        driver.findPermanent(me, "Centaur Courser") shouldBe target
    }
})
