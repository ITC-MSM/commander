package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.CaptainSisay
import com.wingedsheep.mtg.sets.definitions.inv.cards.TsaboTavoc
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tsabo Tavoc (Invasion engine gap #13): protection from a supertype + the {T} destroy-legendary
 * activated ability.
 */
class TsaboTavocScenarioTest : FunSpec({

    val destroyAbilityId = TsaboTavoc.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TsaboTavoc, CaptainSisay))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("{T}: Destroy target legendary creature sends the legendary creature to the graveyard") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)

        val tsabo = driver.putCreatureOnBattlefield(you, "Tsabo Tavoc")
        driver.removeSummoningSickness(tsabo)
        val sisay = driver.putCreatureOnBattlefield(opp, "Captain Sisay") // Legendary Creature

        driver.submit(
            ActivateAbility(
                playerId = you,
                sourceId = tsabo,
                abilityId = destroyAbilityId,
                targets = listOf(ChosenTarget.Permanent(sisay)),
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.getGraveyardCardNames(opp).contains("Captain Sisay") shouldBe true
    }

    test("protection from legendary creatures: Tsabo Tavoc can't be blocked by a legendary creature") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)

        val tsabo = driver.putCreatureOnBattlefield(you, "Tsabo Tavoc")
        driver.removeSummoningSickness(tsabo)
        val sisay = driver.putCreatureOnBattlefield(opp, "Captain Sisay") // legendary blocker

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(tsabo), opp)

        // The legendary creature can't legally block a creature with protection from legendary creatures.
        val result = driver.declareBlockers(opp, mapOf(sisay to listOf(tsabo)))
        result.isSuccess shouldBe false
    }
})
