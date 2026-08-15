package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.MagicPot
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Magic Pot — {3} 1/4 Artifact Creature
 * "When this creature dies, create a Treasure token."
 * "{2}, {T}: Exile target card from a graveyard."
 */
class MagicPotTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MagicPot)
        driver.registerCard(PredefinedTokens.Treasure)
        return driver
    }

    test("creates a Treasure token when it dies") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val pot = driver.putCreatureOnBattlefield(player, "Magic Pot")
        driver.findPermanent(player, "Magic Pot") shouldNotBe null

        // Destroy it with Doom Blade (Magic Pot is colorless, so it's a legal target).
        val doomBlade = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveColorlessMana(player, 1)
        driver.castSpell(player, doomBlade, targets = listOf(pot))
        driver.bothPass() // resolve Doom Blade -> Magic Pot dies -> dies trigger on stack
        driver.bothPass() // resolve the dies trigger -> Treasure token created

        driver.findPermanent(player, "Magic Pot") shouldBe null
        driver.getGraveyardCardNames(player).contains("Magic Pot") shouldBe true
        driver.findPermanent(player, "Treasure") shouldNotBe null
    }
})
