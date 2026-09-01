package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.NullstoneGargoyle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Nullstone Gargoyle — {9} 4/5 Flying (Ravnica: City of Guilds #266)
 *
 * "Whenever the first noncreature spell of a turn is cast, counter that spell."
 *
 * The trigger is "a player casts a noncreature spell" with a `triggerRestriction` reading the
 * whole table's cast history, so the cases that matter are the ones a per-player count would get
 * wrong: the turn's first noncreature spell is countered whoever cast it, the *second* is not
 * even if a different player casts it, a creature spell never opens or closes the window, and a
 * spell cast in response to the trigger is untouched (the 2005 rulings).
 */
class NullstoneGargoyleScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + NullstoneGargoyle)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putCreatureOnBattlefield(d.player1, "Nullstone Gargoyle")
        return d
    }

    fun GameTestDriver.boltInHand(playerId: com.wingedsheep.sdk.model.EntityId): com.wingedsheep.sdk.model.EntityId {
        giveMana(playerId, Color.RED, 1)
        return putCardInHand(playerId, "Lightning Bolt")
    }

    test("the first noncreature spell of the turn is countered, the second is not, creatures don't count") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)

        val courser = d.putCardInHand(d.player1, "Centaur Courser")
        d.giveMana(d.player1, Color.GREEN, 1)
        d.giveColorlessMana(d.player1, 2)
        d.castSpell(d.player1, courser).error shouldBe null
        d.bothPass()
        withClue("a creature spell resolves and does not open the window") {
            (courser in d.state.getBattlefield()) shouldBe true
        }

        val bolt1 = d.boltInHand(d.player1)
        d.castSpell(d.player1, bolt1, targets = listOf(opponent)).error shouldBe null
        d.bothPass()
        withClue("the first noncreature spell is countered") {
            d.getLifeTotal(opponent) shouldBe 20
            (bolt1 in d.getGraveyard(d.player1)) shouldBe true
        }

        val bolt2 = d.boltInHand(d.player1)
        d.castSpell(d.player1, bolt2, targets = listOf(opponent)).error shouldBe null
        d.bothPass()
        withClue("the second noncreature spell resolves untouched") {
            d.getLifeTotal(opponent) shouldBe 17
        }
    }

    test("the count is per turn, not per player: an opponent's first noncreature spell is the turn's second") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)

        val bolt1 = d.boltInHand(d.player1)
        d.castSpell(d.player1, bolt1, targets = listOf(opponent)).error shouldBe null
        d.bothPass()
        d.getLifeTotal(opponent) shouldBe 20

        // Hand priority to the opponent in our main phase; their Bolt is the turn's second
        // noncreature spell even though it is *their* first.
        d.passPriority(d.player1)
        val oppBolt = d.boltInHand(opponent)
        d.castSpell(opponent, oppBolt, targets = listOf(d.player1)).error shouldBe null
        d.bothPass()
        d.getLifeTotal(d.player1) shouldBe 17
    }

    test("a spell cast in response to the trigger is not affected, and the first is still countered") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)

        val bolt1 = d.boltInHand(d.player1)
        d.castSpell(d.player1, bolt1, targets = listOf(opponent)).error shouldBe null

        // Respond to the trigger with a second Bolt: the turn's second noncreature spell.
        val bolt2 = d.boltInHand(d.player1)
        d.castSpell(d.player1, bolt2, targets = listOf(opponent)).error shouldBe null

        d.bothPass()
        withClue("the response resolves normally") {
            d.getLifeTotal(opponent) shouldBe 17
        }
        d.bothPass()
        withClue("the trigger then counters the turn's first noncreature spell") {
            d.getLifeTotal(opponent) shouldBe 17
            (bolt1 in d.getGraveyard(d.player1)) shouldBe true
        }
    }
})
