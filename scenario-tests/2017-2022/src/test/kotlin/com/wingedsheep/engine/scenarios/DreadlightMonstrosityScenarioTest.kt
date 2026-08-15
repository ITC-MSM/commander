package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.vow.cards.DreadlightMonstrosity
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dreadlight Monstrosity (VOW #57) — {4}{U}{U} Creature — Crab Horror, 5/5.
 *
 * "Ward {2}
 *  {3}{U}{U}: This creature can't be blocked this turn. Activate only if you own a card in exile."
 *
 * Covered:
 *   - Ward {2} is present as a keyword.
 *   - The activated ability is gated by [ActivationRestriction.OnlyIfCondition] on "you own a card
 *     in exile" (modeled as `Exists(Player.You, Zone.EXILE)`): it cannot be activated with an empty
 *     exile, but can once a card sits in the player's exile.
 *   - On resolution it grants CANT_BE_BLOCKED until end of turn.
 */
class DreadlightMonstrosityScenarioTest : FunSpec({

    val unblockableAbilityId = DreadlightMonstrosity.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("Dreadlight Monstrosity has ward") {
        val driver = newDriver()
        val player = driver.player1
        val crab = driver.putCreatureOnBattlefield(player, "Dreadlight Monstrosity")
        driver.state.projectedState.hasKeyword(crab, Keyword.WARD) shouldBe true
    }

    test("cannot activate the unblockable ability with an empty exile") {
        val driver = newDriver()
        val player = driver.player1
        val crab = driver.putCreatureOnBattlefield(player, "Dreadlight Monstrosity")
        driver.giveMana(player, Color.BLUE, 5) // enough for {3}{U}{U}

        // No card in the player's exile → the OnlyIfCondition gate fails.
        driver.submitExpectFailure(
            ActivateAbility(playerId = player, sourceId = crab, abilityId = unblockableAbilityId)
        )
        driver.state.projectedState.hasKeyword(crab, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
    }

    test("can activate the unblockable ability while a card is in your exile") {
        val driver = newDriver()
        val player = driver.player1
        val crab = driver.putCreatureOnBattlefield(player, "Dreadlight Monstrosity")
        driver.giveMana(player, Color.BLUE, 5)

        // A card the player owns in exile satisfies "you own a card in exile".
        driver.putCardInExile(player, "Grizzly Bears")

        driver.submit(
            ActivateAbility(playerId = player, sourceId = crab, abilityId = unblockableAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass() // resolve the ability

        driver.state.projectedState.hasKeyword(crab, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
    }
})
