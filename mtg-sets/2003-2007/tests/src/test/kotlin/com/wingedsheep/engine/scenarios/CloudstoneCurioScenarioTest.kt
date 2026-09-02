package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.CloudstoneCurio
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Cloudstone Curio — {3} Artifact (Ravnica: City of Guilds #257)
 *
 * "Whenever a nonartifact permanent you control enters, you may return another permanent you
 *  control that shares a permanent type with it to its owner's hand."
 *
 * The bounce is a battlefield pick over `sharingCardTypeWith(Triggering)` with the entering
 * permanent excluded, so the cases are: a creature entering offers the other creature and not
 * the enchantment; the entering creature itself is never on offer; "you may" is declinable; and
 * an enchantment entering with only creatures around asks nothing at all.
 */
class CloudstoneCurioScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + CloudstoneCurio)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(d.player1, "Cloudstone Curio")
        return d
    }

    fun GameTestDriver.castCourser(): com.wingedsheep.sdk.model.EntityId {
        val courser = putCardInHand(player1, "Centaur Courser")
        giveMana(player1, Color.GREEN, 1)
        giveColorlessMana(player1, 2)
        castSpell(player1, courser).error shouldBe null
        bothPass() // the creature spell resolves; the Curio trigger goes on the stack
        bothPass() // the trigger resolves and asks
        return courser
    }

    test("a creature entering may bounce another creature you control, never the enchantment or itself") {
        val d = driver()
        val lions = d.putCreatureOnBattlefield(d.player1, "Savannah Lions")
        val enchantment = d.putPermanentOnBattlefield(d.player1, "Test Enchantment")
        val courser = d.castCourser()

        withClue("the Curio asks which permanent to return") {
            d.state.pendingDecision shouldNotBe null
        }
        withClue("the enchantment shares no permanent type with a creature") {
            d.submitCardSelection(d.player1, listOf(enchantment)).error shouldNotBe null
        }
        withClue("the entering creature is not 'another' permanent") {
            d.submitCardSelection(d.player1, listOf(courser)).error shouldNotBe null
        }

        d.submitCardSelection(d.player1, listOf(lions)).error shouldBe null
        withClue("the Lions went back to hand; the Courser and the enchantment stayed") {
            (lions in d.getHand(d.player1)) shouldBe true
            (courser in d.state.getBattlefield()) shouldBe true
            (enchantment in d.state.getBattlefield()) shouldBe true
        }
    }

    test("\"you may\" — choosing nothing returns nothing") {
        val d = driver()
        val lions = d.putCreatureOnBattlefield(d.player1, "Savannah Lions")
        d.castCourser()

        d.submitCardSelection(d.player1, emptyList()).error shouldBe null
        (lions in d.state.getBattlefield()) shouldBe true
        d.state.pendingDecision shouldBe null
    }

    test("an entering permanent with nothing sharing its type asks nothing") {
        val d = driver()
        val lions = d.putCreatureOnBattlefield(d.player1, "Savannah Lions")
        val enchantment = d.putCardInHand(d.player1, "Test Enchantment")
        d.giveMana(d.player1, Color.WHITE, 1)
        d.giveColorlessMana(d.player1, 1)
        d.castSpell(d.player1, enchantment).error shouldBe null
        d.bothPass()
        d.bothPass()

        withClue("only creatures on the battlefield — no candidate, no prompt") {
            d.state.pendingDecision shouldBe null
            (lions in d.state.getBattlefield()) shouldBe true
            (enchantment in d.state.getBattlefield()) shouldBe true
        }
    }
})
