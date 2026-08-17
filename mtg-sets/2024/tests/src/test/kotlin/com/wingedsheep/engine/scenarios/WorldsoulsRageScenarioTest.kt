package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.WorldsoulsRage
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Worldsoul's Rage (MKM #244) — {X}{R}{G} Sorcery.
 *
 * "Worldsoul's Rage deals X damage to any target. Put up to X land cards from your hand and/or
 *  graveyard onto the battlefield tapped."
 *
 * One X drives two unrelated effects, which is where this card can go wrong: the damage and the land
 * count must read the *same* X, the land selection must span both zones in one decision, the lands
 * must arrive tapped, and "up to X" must tolerate both a partial choice and X = 0.
 */
class WorldsoulsRageScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + WorldsoulsRage)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** {X}{R}{G} with the given X. */
    fun GameTestDriver.payForRage(player: EntityId, x: Int) {
        giveMana(player, Color.RED, 1)
        giveMana(player, Color.GREEN, 1)
        if (x > 0) giveColorlessMana(player, x)
    }

    test("X=2 deals 2 damage and puts two lands from hand and graveyard onto the battlefield tapped") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        // A 2/2 that exactly dies to X=2.
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        // One land in each zone, so the selection has to span both.
        driver.putCardInHand(player, "Forest")
        driver.putCardInGraveyard(player, "Island")

        val landsBefore = driver.getLands(player).size
        val rage = driver.putCardInHand(player, "Worldsoul's Rage")
        driver.payForRage(player, 2)
        driver.castXSpell(
            player,
            rage,
            xValue = 2,
            targets = listOf(driver.findPermanent(opponent, "Grizzly Bears")!!)
        ).error shouldBe null
        driver.bothPass()

        // The damage lands first, but the 2/2 only leaves the battlefield when state-based actions
        // next run — which is after this resolution finishes, not while it is paused on the land
        // selection. So the selection is what we see here.
        val selection = driver.pendingDecision as SelectCardsDecision
        val offered = selection.options.mapNotNull { driver.getCardName(it) }
        withClue("one decision spans both zones: the Forest in hand and the Island in the graveyard") {
            offered.contains("Forest") shouldBe true
            offered.contains("Island") shouldBe true
        }
        val crossZone = selection.options.filter { driver.getCardName(it) in setOf("Forest", "Island") }
        driver.submitCardSelection(player, crossZone).error shouldBe null
        repeat(3) { if (driver.stackSize > 0 || driver.pendingDecision != null) driver.bothPass() }

        withClue("X damage killed the 2/2 once state-based actions ran") {
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
        }

        val lands = driver.getLands(player)
        withClue("both lands arrived") {
            lands.size shouldBe landsBefore + 2
        }
        withClue("and both arrived tapped") {
            val arrived = lands.filter { driver.getCardName(it) in setOf("Forest", "Island") }
            arrived.size shouldBe 2
            arrived.all { driver.isTapped(it) } shouldBe true
        }
    }

    test("'up to X' allows putting fewer lands than X") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.putCardInHand(player, "Forest")
        driver.putCardInGraveyard(player, "Island")

        val landsBefore = driver.getLands(player).size
        val rage = driver.putCardInHand(player, "Worldsoul's Rage")
        driver.payForRage(player, 2)
        driver.castXSpell(
            player,
            rage,
            xValue = 2,
            targets = listOf(driver.findPermanent(opponent, "Grizzly Bears")!!)
        ).error shouldBe null
        driver.bothPass()

        // Take only the one in hand, leaving the graveyard land behind.
        val selection = driver.pendingDecision as SelectCardsDecision
        val fromHand = selection.options.first { driver.getCardName(it) == "Forest" }
        driver.submitCardSelection(player, listOf(fromHand)).error shouldBe null
        repeat(3) { if (driver.stackSize > 0 || driver.pendingDecision != null) driver.bothPass() }

        withClue("exactly the one chosen land arrived") {
            driver.getLands(player).size shouldBe landsBefore + 1
        }
        withClue("the declined graveyard land stayed put") {
            driver.getGraveyardCardNames(player).contains("Island") shouldBe true
        }
    }

    test("X=0 is a legal cast that deals no damage and puts no lands") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.putCardInHand(player, "Forest")

        val landsBefore = driver.getLands(player).size
        val rage = driver.putCardInHand(player, "Worldsoul's Rage")
        driver.payForRage(player, 0)
        driver.castXSpell(player, rage, xValue = 0, targets = listOf(bear)).error shouldBe null
        repeat(4) {
            when (val decision = driver.pendingDecision) {
                is SelectCardsDecision -> driver.submitCardSelection(player, emptyList())
                else -> if (driver.stackSize > 0 || decision != null) driver.bothPass() else Unit
            }
        }

        withClue("zero damage leaves the 2/2 alive") {
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe bear
        }
        withClue("and no land could be put onto the battlefield") {
            driver.getLands(player).size shouldBe landsBefore
        }
    }
})
