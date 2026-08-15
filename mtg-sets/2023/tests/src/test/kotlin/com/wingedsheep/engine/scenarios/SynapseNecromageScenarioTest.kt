package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SynapseNecromage
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Synapse Necromage (LCI #125): {2}{B} 3/1 Fungus Wizard
 *
 * "When this creature dies, create two 1/1 black Fungus creature tokens with
 * 'This token can't block.'"
 *
 * Tests:
 * 1. When Synapse Necromage dies, exactly two 1/1 black Fungus tokens are created,
 *    each with the "can't block" static ability.
 */
class SynapseNecromageScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SynapseNecromage))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    fun GameTestDriver.fungusTokens(playerId: EntityId): List<EntityId> =
        getCreatures(playerId).filter { getCardName(it) == "Fungus Token" }

    test("dying Synapse Necromage creates two 1/1 black Fungus can't-block tokens") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val necromage = driver.putCreatureOnBattlefield(player, "Synapse Necromage")
        val tokensBefore = driver.fungusTokens(player).size

        // Kill the 3/1 Necromage with a Lightning Bolt so it dies through the real
        // damage/SBA path → the dies trigger is detected and queued.
        driver.giveMana(player, Color.RED, 1)
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.castSpellWithTargets(player, bolt, listOf(ChosenTarget.Permanent(necromage)))
        driver.bothPass() // resolve the bolt -> Necromage dies, queuing its dies trigger
        driver.bothPass() // resolve the dies trigger -> creates the two Fungus tokens

        val tokensAfter = driver.fungusTokens(player)
        tokensAfter.size shouldBe tokensBefore + 2

        tokensAfter.forEach { token ->
            driver.state.projectedState.getPower(token) shouldBe 1
            driver.state.projectedState.getToughness(token) shouldBe 1
            driver.state.projectedState.cantBlock(token) shouldBe true
        }
    }
})
