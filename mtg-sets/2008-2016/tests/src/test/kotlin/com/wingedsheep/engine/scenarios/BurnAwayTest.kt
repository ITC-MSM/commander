package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.BurnAway
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Tests for Burn Away (KTK #104).
 *
 * Burn Away: {4}{R}
 * Instant
 * Burn Away deals 6 damage to target creature. When that creature dies this turn,
 * exile its controller's graveyard.
 */
class BurnAwayTest : FunSpec({

    val bigBeast = CardDefinition.creature(
        name = "Big Beast",
        manaCost = ManaCost.parse("{5}{G}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 7,
        toughness = 7
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BurnAway, bigBeast))
        return driver
    }

    test("Burn Away kills creature and exiles controller's graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put cards in opponent's graveyard
        val graveyardCard1 = driver.putCardInGraveyard(opponent, "Grizzly Bears")
        val graveyardCard2 = driver.putCardInGraveyard(opponent, "Giant Growth")

        // Put a 2/2 creature on opponent's battlefield (dies to 6 damage)
        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Cast Burn Away targeting the creature
        driver.giveMana(activePlayer, Color.RED, 5)
        val burnAway = driver.putCardInHand(activePlayer, "Burn Away")
        driver.castSpellWithTargets(activePlayer, burnAway, listOf(ChosenTarget.Permanent(creature)))
        driver.bothPass()

        // Creature should be dead (in exile since graveyard was exiled)
        val graveyardZone = ZoneKey(opponent, Zone.GRAVEYARD)
        val exileZone = ZoneKey(opponent, Zone.EXILE)

        // Graveyard should be empty - all cards exiled
        driver.state.getZone(graveyardZone).shouldBeEmpty()

        // All graveyard cards should be in exile (including the dead creature)
        driver.state.getZone(exileZone) shouldContain graveyardCard1
        driver.state.getZone(exileZone) shouldContain graveyardCard2
        driver.state.getZone(exileZone) shouldContain creature
    }

    test("Burn Away deals 6 damage but creature survives - graveyard not exiled") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put cards in opponent's graveyard
        val graveyardCard = driver.putCardInGraveyard(opponent, "Giant Growth")

        // Put a 7/7 creature on opponent's battlefield (survives 6 damage)
        val creature = driver.putCreatureOnBattlefield(opponent, "Big Beast")

        // Cast Burn Away targeting the creature
        driver.giveMana(activePlayer, Color.RED, 5)
        val burnAway = driver.putCardInHand(activePlayer, "Burn Away")
        driver.castSpellWithTargets(activePlayer, burnAway, listOf(ChosenTarget.Permanent(creature)))
        driver.bothPass()

        // Creature should have 6 damage but still be alive
        val damage = driver.state.getEntity(creature)?.get<DamageComponent>()?.amount ?: 0
        damage shouldBe 6

        // Graveyard should NOT be exiled since the creature survived
        val graveyardZone = ZoneKey(opponent, Zone.GRAVEYARD)
        driver.state.getZone(graveyardZone) shouldContain graveyardCard
    }
})
