package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.AltanakTheThriceCalled
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Altanak, the Thrice-Called (DSK #166) — {5}{G}{G} 9/9 Legendary Creature — Insect Beast.
 *
 *  - Trample (keyword; covered by snapshot).
 *  - "Whenever Altanak becomes the target of a spell or ability an opponent controls, draw a card."
 *  - "{1}{G}, Discard this card: Return target land card from your graveyard to the battlefield
 *    tapped." — a from-hand activated ability ([Costs.DiscardSelf], activateFromZone = HAND).
 */
class AltanakTheThriceCalledScenarioTest : FunSpec({

    test("opponent targeting Altanak draws its controller a card") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Active player (player1) targets player2's Altanak with Doom Blade.
        val caster = driver.player1
        val controller = driver.player2
        val altanak = driver.putCreatureOnBattlefield(controller, "Altanak, the Thrice-Called")
        val doomBlade = driver.putCardInHand(caster, "Doom Blade")
        driver.giveMana(caster, Color.BLACK, 2)

        val handBefore = driver.getHand(controller).size

        driver.castSpell(caster, doomBlade, targets = listOf(altanak)).isSuccess shouldBe true
        // The targeting trigger goes on the stack above Doom Blade; resolve everything.
        driver.bothPass()

        // Altanak's controller (player2) drew a card from the targeting trigger.
        driver.getHand(controller).size shouldBe handBefore + 1
    }

    test("your own spell targeting Altanak does not trigger the draw") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.player1
        val altanak = driver.putCreatureOnBattlefield(player, "Altanak, the Thrice-Called")
        val giantGrowth = driver.putCardInHand(player, "Giant Growth")
        driver.giveMana(player, Color.GREEN, 1)

        val handBefore = driver.getHand(player).size

        driver.castSpell(player, giantGrowth, targets = listOf(altanak)).isSuccess shouldBe true
        driver.bothPass()

        // No draw — the trigger only fires for an opponent's spell/ability. The hand only shrinks
        // by the Giant Growth that was cast.
        driver.getHand(player).size shouldBe handBefore - 1
    }

    test("from-hand ability returns a land from graveyard to the battlefield tapped, discarding Altanak") {
        val abilityId = AltanakTheThriceCalled.activatedAbilities.first().id

        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val altanak = driver.putCardInHand(player, "Altanak, the Thrice-Called")
        val graveLand = driver.putCardInGraveyard(player, "Forest")
        driver.giveMana(player, Color.GREEN, 2)

        // Activate the from-hand ability, targeting the graveyard Forest and paying {1}{G} from the
        // mana pool. The target is a graveyard card (ChosenTarget.Card in the GRAVEYARD zone).
        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = altanak,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Card(graveLand, player, Zone.GRAVEYARD)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true

        driver.bothPass()

        // Altanak was discarded as a cost; the land left the graveyard for the battlefield tapped.
        driver.getHand(player).contains(altanak) shouldBe false
        driver.getGraveyard(player).contains(altanak) shouldBe true
        driver.getGraveyard(player).contains(graveLand) shouldBe false
        driver.findPermanent(player, "Forest") shouldBe graveLand
    }
})
