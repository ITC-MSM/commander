package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.Ultima
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Engine coverage for Ultima ({3}{W}{W} sorcery — "Destroy all artifacts and creatures. End the
 * turn.") and the underlying [com.wingedsheep.sdk.dsl.Effects.EndTheTurn] effect (CR 720).
 *
 * The tests pin the four observable consequences of ending the turn: the board wipe resolves, the
 * dies triggers from that wipe never reach the stack (CR 720.1c), Ultima exiles itself (CR 720.1a),
 * and the turn ends into the opponent's turn — with the cleanup step's hand-size discard applied.
 */
class UltimaScenarioTest : FunSpec({

    // A minimal non-creature artifact, to prove "all artifacts" (not just artifact creatures) go.
    val testRelic = CardDefinition(
        name = "Test Relic",
        manaCost = ManaCost.parse("{1}"),
        typeLine = TypeLine.artifact()
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + Ultima + testRelic)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        return driver
    }

    test("Ultima destroys all artifacts and creatures, exiles itself, and ends the turn") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A full board: creatures for both players, an artifact creature, and a non-creature relic.
        driver.putCreatureOnBattlefield(p1, "Savannah Lions")
        driver.putCreatureOnBattlefield(p2, "Centaur Courser")
        driver.putCreatureOnBattlefield(p1, "Artifact Creature")
        driver.putPermanentOnBattlefield(p1, "Test Relic")

        val ultima = driver.putCardInHand(p1, "Ultima")
        driver.giveMana(p1, Color.WHITE, 2)
        driver.giveColorlessMana(p1, 3)

        driver.castSpell(p1, ultima)
        driver.bothPass() // resolve Ultima -> destroy all -> end the turn

        // Every artifact and creature is destroyed.
        driver.getCreatures(p1).shouldBeEmpty()
        driver.getCreatures(p2).shouldBeEmpty()
        driver.findPermanent(p1, "Artifact Creature") shouldBe null
        driver.findPermanent(p1, "Test Relic") shouldBe null

        // CR 720.1a: Ultima is exiled along with the stack, not put into the graveyard.
        driver.getExileCardNames(p1) shouldContain "Ultima"
        driver.getGraveyardCardNames(p1) shouldNotContain "Ultima"

        // The turn ended: it is now the opponent's turn.
        driver.activePlayer shouldBe p2
    }

    test("The dies triggers from Ultima's board wipe never resolve (CR 720.1c)") {
        val driver = createDriver()
        val p1 = driver.player1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // "When this creature dies, you gain 3 life." If the trigger resolved, p1 would reach 23.
        driver.putCreatureOnBattlefield(p1, "Death Trigger Test Creature")

        val ultima = driver.putCardInHand(p1, "Ultima")
        driver.giveMana(p1, Color.WHITE, 2)
        driver.giveColorlessMana(p1, 3)

        driver.castSpell(p1, ultima)
        driver.bothPass()

        // The creature was destroyed, but its dies trigger was exiled with the stack, so no life gain.
        driver.findPermanent(p1, "Death Trigger Test Creature") shouldBe null
        driver.getLifeTotal(p1) shouldBe 20
    }

    test("Ending the turn makes the active player discard down to their maximum hand size") {
        val driver = createDriver()
        val p1 = driver.player1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ultima = driver.putCardInHand(p1, "Ultima")
        // Ten spare cards in hand; after Ultima leaves for the stack the cleanup step must trim to 7.
        repeat(10) { driver.putCardInHand(p1, "Savannah Lions") }
        driver.giveMana(p1, Color.WHITE, 2)
        driver.giveColorlessMana(p1, 3)

        driver.castSpell(p1, ultima)
        driver.bothPass() // resolve -> end the turn -> cleanup pauses for the hand-size discard

        // The cleanup step raised the discard; resolve it and confirm the hand was trimmed to seven.
        driver.autoResolveDecision()
        driver.getHand(p1) shouldHaveSize 7
    }
})
