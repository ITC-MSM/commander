package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SanguineEvangelist
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Sanguine Evangelist {2}{W} — Creature — Vampire Cleric 2/1 (LCI #34)
 *
 * Battle cry (Whenever this creature attacks, each other attacking creature gets +1/+0 until
 * end of turn.)
 * When this creature enters or dies, create a 1/1 black Bat creature token with flying.
 *
 * Coverage:
 * 1. Battle cry — every OTHER attacking creature gets +1/+0; the Evangelist itself does not,
 *    and a creature that stays home is untouched (the "attacking" filter).
 * 2. Enters — casting it creates a 1/1 black flying Bat token.
 * 3. Dies — killing it (Lightning Bolt to the 2/1) creates a second Bat token.
 */
class SanguineEvangelistScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SanguineEvangelist))
        return driver
    }

    fun GameTestDriver.batTokens(playerId: EntityId): List<EntityId> =
        getPermanents(playerId).filter { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == "Bat Token"
        }

    test("battle cry: each other attacking creature gets +1/+0, but not the Evangelist or a home creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val evangelist = driver.putCreatureOnBattlefield(me, "Sanguine Evangelist") // 2/1
        val courser = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3, attacks
        val homebody = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3, stays home
        listOf(evangelist, courser, homebody).forEach { driver.removeSummoningSickness(it) }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(evangelist, courser), opp).error shouldBe null
        // Resolve the battle cry trigger.
        driver.bothPass()

        val projected = driver.state.projectedState
        // Other attacking creature is pumped: 3/3 -> 4/3.
        projected.getPower(courser) shouldBe 4
        projected.getToughness(courser) shouldBe 3
        // The Evangelist itself is not pumped ("each OTHER attacking creature").
        projected.getPower(evangelist) shouldBe 2
        projected.getToughness(evangelist) shouldBe 1
        // A creature that didn't attack is untouched.
        projected.getPower(homebody) shouldBe 3
        projected.getToughness(homebody) shouldBe 3
    }

    test("enters: casting Sanguine Evangelist creates a 1/1 black flying Bat token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.batTokens(me).size shouldBe 0

        driver.giveMana(me, Color.WHITE, 3) // {2}{W}
        val card = driver.putCardInHand(me, "Sanguine Evangelist")
        driver.castSpell(me, card).error shouldBe null
        driver.bothPass() // resolve the creature spell -> it enters
        driver.bothPass() // resolve the enters trigger -> Bat token created

        driver.findPermanent(me, "Sanguine Evangelist").shouldNotBeNull()
        val bats = driver.batTokens(me)
        bats.size shouldBe 1
        val bat = bats.first()
        val projected = driver.state.projectedState
        projected.getPower(bat) shouldBe 1
        projected.getToughness(bat) shouldBe 1
        projected.hasKeyword(bat, Keyword.FLYING) shouldBe true
        driver.state.getEntity(bat)?.get<CardComponent>()?.colors shouldBe setOf(Color.BLACK)
    }

    test("dies: destroying Sanguine Evangelist creates a Bat token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val evangelist = driver.putCreatureOnBattlefield(me, "Sanguine Evangelist") // 2/1
        driver.batTokens(me).size shouldBe 0

        driver.giveMana(me, Color.RED, 1)
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.castSpell(me, bolt, targets = listOf(evangelist)).error shouldBe null
        driver.bothPass() // resolve Bolt -> 3 damage -> SBA kills the 2/1
        driver.findPermanent(me, "Sanguine Evangelist") shouldBe null
        driver.bothPass() // resolve the dies trigger -> Bat token created

        driver.batTokens(me).size shouldBe 1
    }
})
