package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Recollect (RAV #178) — {2}{G} Sorcery, "Return target card from **your** graveyard to your hand."
 *
 * The possessive is the point. The card shipped filtering on `TargetFilter.CardInGraveyard`, which
 * is *any* graveyard, so Recollect could reach into an opponent's. A generated render that dropped
 * the word. Found by the Argentum Assay differential gate, which read the printed sentence and
 * diffed its reading against the committed definition; Elven Cache and Déjà Vu, the other two cards
 * printing this sentence, both scope it to the caster's own graveyard.
 *
 * Both halves are asserted, because only the second would have failed before the fix: a card in your
 * own graveyard comes back, and one in an opponent's is not a legal target at all (CR 115.4 — an
 * illegal target can't be chosen), so the cast declaring it is rejected outright.
 */
class RecollectScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Forest" to 30))
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /**
     * Recollect in hand, the mana its cost wants, and the graveyard card it is aimed at.
     *
     * A card in a graveyard is not a permanent, so the target is declared as [ChosenTarget.Card] —
     * the cast-time variant that carries the zone and the owner the requirement is checked against.
     */
    fun castRecollect(d: GameTestDriver, owner: EntityId, card: EntityId) = run {
        val recollect = d.putCardInHand(d.player1, "Recollect")
        d.giveMana(d.player1, Color.GREEN, 3)
        d.castSpellWithTargets(
            d.player1,
            recollect,
            listOf(ChosenTarget.Card(cardId = card, ownerId = owner, zone = Zone.GRAVEYARD)),
        )
    }

    test("it returns a card from your own graveyard to your hand") {
        val d = driver()
        val mine = d.putCardInGraveyard(d.player1, "Grizzly Bears")

        castRecollect(d, d.player1, mine).isSuccess shouldBe true
        while (d.stackSize > 0) d.bothPass()

        d.getGraveyardCardNames(d.player1) shouldNotContain "Grizzly Bears"
        d.getHand(d.player1) shouldContain mine
    }

    // The regression the gate found. `CardInGraveyard` is unowned, so an opponent's graveyard was
    // just as legal a source as your own and this cast used to succeed.
    test("a card in an opponent's graveyard is not a legal target") {
        val d = driver()
        d.putCardInGraveyard(d.player1, "Grizzly Bears")
        val theirs = d.putCardInGraveyard(d.player2, "Centaur Courser")

        castRecollect(d, d.player2, theirs).isSuccess shouldBe false
        d.getGraveyardCardNames(d.player2) shouldContain "Centaur Courser"
    }
})
