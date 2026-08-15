package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.SplinterRadicalRat
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for Splinter, Radical Rat (TMT #169).
 *
 * {1}{U}: Target Ninja can't be blocked this turn.
 *
 * Guards against the inverted-ability bug where the activated ability granted
 * CantBlock (preventing the Ninja from blocking) instead of CANT_BE_BLOCKED
 * (the printed evasion). Splinter is himself a Ninja, so he is a legal target.
 */
class SplinterRadicalRatTest : FunSpec({

    val splinterAbilityId = SplinterRadicalRat.activatedAbilities.first().id

    test("{1}{U} grants CANT_BE_BLOCKED and the targeted Ninja can't be blocked") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 20), startingLife = 20)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val splinter = driver.putCreatureOnBattlefield(player, "Splinter, Radical Rat")
        driver.removeSummoningSickness(splinter)

        val blocker = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(blocker)

        // Sanity: no evasion before the ability is used.
        driver.state.projectedState.hasKeyword(splinter, AbilityFlag.CANT_BE_BLOCKED) shouldBe false

        // Attack with Splinter, then activate {1}{U} targeting himself.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(splinter), opponent)

        driver.giveMana(player, Color.BLUE, 2)
        val result = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = splinter,
                abilityId = splinterAbilityId,
                targets = listOf(ChosenTarget.Permanent(splinter))
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass() // resolve the GrantKeyword effect

        // Splinter is now unblockable, not unable to block.
        driver.state.projectedState.hasKeyword(splinter, AbilityFlag.CANT_BE_BLOCKED) shouldBe true

        // Attempting to block Splinter must fail.
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        val blockResult = driver.declareBlockers(opponent, mapOf(blocker to listOf(splinter)))
        blockResult.isSuccess shouldBe false
    }
})
