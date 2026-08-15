package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.DauthiSlayer
import com.wingedsheep.mtg.sets.definitions.tmp.cards.MazeOfShadows
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Maze of Shadows (TMP #319)
 * Land
 * {T}: Add {C}.
 * {T}: Untap target attacking creature with shadow. Prevent all combat damage
 *      that would be dealt to and dealt by that creature this turn.
 */
class MazeOfShadowsScenarioTest : FunSpec({

    val manaAbilityId = MazeOfShadows.activatedAbilities[0].id
    val untapAbilityId = MazeOfShadows.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MazeOfShadows)
        driver.registerCard(DauthiSlayer)
        return driver
    }

    test("the mana ability adds {C}") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val maze = driver.putPermanentOnBattlefield(activePlayer, "Maze of Shadows")

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = maze, abilityId = manaAbilityId)
        )
        result.isSuccess shouldBe true
        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.colorless shouldBe 1
    }

    test("untaps a tapped attacking creature with shadow") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        val maze = driver.putPermanentOnBattlefield(activePlayer, "Maze of Shadows")
        val slayer = driver.putPermanentOnBattlefield(activePlayer, "Dauthi Slayer")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(slayer), opponent).isSuccess shouldBe true

        // Attacking taps the creature (no vigilance).
        driver.isTapped(slayer) shouldBe true

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = maze,
                abilityId = untapAbilityId,
                targets = listOf(ChosenTarget.Permanent(slayer)),
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass() // resolve the ability

        // The shadow attacker is now untapped.
        driver.isTapped(slayer) shouldBe false
    }

    test("cannot target a non-shadow attacking creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        val maze = driver.putPermanentOnBattlefield(activePlayer, "Maze of Shadows")
        val courser = driver.putPermanentOnBattlefield(activePlayer, "Centaur Courser")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(courser), opponent).isSuccess shouldBe true

        // Centaur Courser has no shadow, so it is not a legal target.
        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = maze,
                abilityId = untapAbilityId,
                targets = listOf(ChosenTarget.Permanent(courser)),
            )
        )
    }
})
