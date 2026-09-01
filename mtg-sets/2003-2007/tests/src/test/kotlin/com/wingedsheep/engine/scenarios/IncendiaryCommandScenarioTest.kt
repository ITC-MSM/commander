package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.IncendiaryCommand
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Incendiary Command (LRW #179) — {3}{R}{R} Sorcery.
 *
 * Choose two —
 * • Incendiary Command deals 4 damage to target player or planeswalker.
 * • Incendiary Command deals 2 damage to each creature.
 * • Destroy target nonbasic land.
 * • Each player discards all the cards in their hand, then draws that many cards.
 *
 * The wheel mode is the one worth proving. "Each player discards all the cards in their hand, then
 * draws **that many**" is per-player, not a shared count, so it runs inside a `ForEachPlayerEffect`
 * whose fresh per-iteration stored collections are what make `discardedHand_count` mean *that*
 * player's hand. Hoisting the discard out of the loop — or letting one iteration's count leak into
 * the next — would draw everyone the same number of cards, which is invisible whenever the two
 * hands happen to be the same size. So the test gives the two players deliberately *different*
 * hand sizes, the one shape where the two readings disagree.
 *
 * The nonbasic-land mode gets its own test because "nonbasic" is a fail-open axis: a filter that
 * dropped the supertype check would happily destroy a Mountain and nothing about the card would
 * look wrong.
 */
class IncendiaryCommandScenarioTest : FunSpec({

    // Mode order follows the printed bullets.
    val damagePlayer = 0
    val sweepCreatures = 1
    val destroyLand = 2
    val wheel = 3

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(IncendiaryCommand))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castCommand(
        caster: EntityId,
        modes: List<Int>,
        modeTargets: List<List<ChosenTarget>> = emptyList(),
    ): ExecutionResult {
        giveMana(caster, Color.RED, 5)
        val spell = putCardInHand(caster, "Incendiary Command")
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

    test("each player wheels their own hand — unequal hands stay unequal") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // Empty both hands, then deal deliberately different sizes. The Command itself is put into
        // the caster's hand by castCommand and leaves it on the cast, so it is not counted here.
        driver.getHand(me).forEach { driver.moveToGraveyard(it) }
        driver.getHand(opp).forEach { driver.moveToGraveyard(it) }
        repeat(4) { driver.putCardInHand(me, "Grizzly Bears") }
        repeat(1) { driver.putCardInHand(opp, "Grizzly Bears") }

        driver.castCommand(me, modes = listOf(wheel, sweepCreatures)).error shouldBe null
        driver.bothPass()

        driver.getHandSize(me) shouldBe 4
        driver.getHandSize(opp) shouldBe 1
        // The discarded hands went to each player's own graveyard.
        driver.getGraveyardCardNames(me).count { it == "Grizzly Bears" } shouldBe 4
        driver.getGraveyardCardNames(opp).count { it == "Grizzly Bears" } shouldBe 1
    }

    test("a player with an empty hand draws nothing rather than the other player's count") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.getHand(me).forEach { driver.moveToGraveyard(it) }
        driver.getHand(opp).forEach { driver.moveToGraveyard(it) }
        repeat(3) { driver.putCardInHand(me, "Grizzly Bears") }

        driver.castCommand(me, modes = listOf(wheel, sweepCreatures)).error shouldBe null
        driver.bothPass()

        driver.getHandSize(me) shouldBe 3
        driver.getHandSize(opp) shouldBe 0
    }

    test("2 damage to each creature is symmetric and kills only what it should") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(me, "Savannah Lions")    // 1/1 — dies
        driver.putCreatureOnBattlefield(me, "Centaur Courser")   // 3/3 — survives
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")    // 2/2 — dies

        driver.castCommand(me, modes = listOf(sweepCreatures, damagePlayer), modeTargets = listOf(
            emptyList(),
            listOf(ChosenTarget.Player(opp)),
        )).error shouldBe null
        driver.bothPass()

        driver.getCreatures(me).map { driver.getCardName(it) } shouldBe listOf("Centaur Courser")
        driver.getCreatures(opp).size shouldBe 0
        driver.getLifeTotal(opp) shouldBe 16
    }

    test("the land mode takes a nonbasic land and leaves a basic alone") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.putLandOnBattlefield(opp, "Mountain")
        val nonbasic = driver.putLandOnBattlefield(opp, "Vivid Crag")

        driver.castCommand(
            me,
            modes = listOf(destroyLand, damagePlayer),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(nonbasic)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.getLands(opp).map { driver.getCardName(it) } shouldBe listOf("Mountain")
    }
})
