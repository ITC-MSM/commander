package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.BloodbondMarch
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Bloodbond March (RAV #192) — "Whenever a player casts a creature spell, each player returns all
 * cards with the same name as that spell from their graveyard to the battlefield."
 *
 * The assertions that matter: every graveyard is read (the caster's *and* the opponent's), the
 * returned cards come back under their own owners' control, cards with another name stay put,
 * and the trigger resolves before the spell so the freshly cast creature is never among them.
 */
class BloodbondMarchScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BloodbondMarch)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.countOnBattlefield(player: EntityId, name: String): Int =
        state.getBattlefield(player).count { getCardName(it) == name }

    test("casting a creature returns every same-named card from every graveyard under its owner's control") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        d.putPermanentOnBattlefield(me, "Bloodbond March")
        d.putCardInGraveyard(me, "Grizzly Bears")
        d.putCardInGraveyard(opp, "Grizzly Bears")
        d.putCardInGraveyard(opp, "Grizzly Bears")
        d.putCardInGraveyard(opp, "Centaur Courser")

        val bears = d.putCardInHand(me, "Grizzly Bears")
        d.giveMana(me, Color.GREEN, 1)
        d.giveColorlessMana(me, 1)
        d.castSpell(me, bears).isSuccess shouldBe true

        withClue("the trigger sits above the creature spell") { d.stackSize shouldBe 2 }
        d.bothPass()
        withClue("the trigger resolved first; the cast Bears is still on the stack") {
            d.stackSize shouldBe 1
            d.countOnBattlefield(me, "Grizzly Bears") shouldBe 1
            d.countOnBattlefield(opp, "Grizzly Bears") shouldBe 2
        }
        d.bothPass()

        withClue("each returned card is under its own owner's control, plus the cast one") {
            d.countOnBattlefield(me, "Grizzly Bears") shouldBe 2
            d.countOnBattlefield(opp, "Grizzly Bears") shouldBe 2
        }
        withClue("only same-named cards left the graveyards") {
            d.getGraveyardCardNames(me) shouldNotContain "Grizzly Bears"
            d.getGraveyardCardNames(opp) shouldNotContain "Grizzly Bears"
            d.getGraveyardCardNames(opp) shouldContain "Centaur Courser"
            d.countOnBattlefield(opp, "Centaur Courser") shouldBe 0
        }
    }

    test("a noncreature spell does not trigger it") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        d.putPermanentOnBattlefield(me, "Bloodbond March")
        d.putCardInGraveyard(opp, "Grizzly Bears")

        val bolt = d.putCardInHand(me, "Lightning Bolt")
        d.giveMana(me, Color.RED, 1)
        d.castSpellWithTargets(me, bolt, listOf(ChosenTarget.Player(opp))).isSuccess shouldBe true
        withClue("no trigger on the stack") { d.stackSize shouldBe 1 }
        d.bothPass()

        d.getGraveyardCardNames(opp) shouldContain "Grizzly Bears"
        d.countOnBattlefield(opp, "Grizzly Bears") shouldBe 0
    }
})
