package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.chk.cards.HanaKami
import com.wingedsheep.mtg.sets.definitions.chk.cards.KodamasMight
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Hana Kami (CHK #211) — {G} Creature — Spirit 1/1.
 *
 *   {1}{G}, Sacrifice this creature: Return target Arcane card from your graveyard to your hand.
 *
 * **The first card in the corpus to filter on `Subtype.ARCANE`.** `Subtype.ARCANE` has existed in
 * `mtg-sdk` for a long time, but until this sweep nothing read it, so a filter that silently matched
 * every card in the graveyard — or matched nothing at all — would have looked identical from the
 * outside. Both directions are asserted here: an Arcane card must be a legal target, and a
 * non-Arcane card in the same graveyard must not be.
 *
 * Note also that Argentum Assay *declines* this card's line — its grammar has no rule for the noun
 * phrase "an Arcane card" — so it was authored from the printed text rather than from `assay
 * compile`. That makes this test the only mechanical check on the wiring.
 */
class HanaKamiScenarioTest : FunSpec({

    val returnAbility = HanaKami.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + HanaKami + KodamasMight)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("returns an Arcane card from your graveyard to your hand") {
        val d = driver()
        val kami = d.putCreatureOnBattlefield(d.player1, "Hana Kami")
        // Kodama's Might is {G} Instant — Arcane.
        val might = d.putCardInGraveyard(d.player1, "Kodama's Might")
        d.giveMana(d.player1, Color.GREEN, 1)
        d.giveColorlessMana(d.player1, 1)

        d.submit(
            ActivateAbility(
                d.player1,
                kami,
                returnAbility,
                targets = listOf(ChosenTarget.Card(might, d.player1, Zone.GRAVEYARD)),
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("the Arcane card moves to hand") {
            d.getGraveyardCardNames(d.player1) shouldNotContain "Kodama's Might"
        }
        withClue("Hana Kami sacrificed itself as part of the cost") {
            d.state.getBattlefield().contains(kami) shouldBe false
            d.getGraveyardCardNames(d.player1) shouldContain "Hana Kami"
        }
    }

    test("a non-Arcane card in the same graveyard is not a legal target") {
        val d = driver()
        val kami = d.putCreatureOnBattlefield(d.player1, "Hana Kami")
        // Grizzly Bears is a Creature — Bear: in the graveyard, owned by you, but not Arcane.
        val bears = d.putCardInGraveyard(d.player1, "Grizzly Bears")
        d.giveMana(d.player1, Color.GREEN, 1)
        d.giveColorlessMana(d.player1, 1)

        val result = d.submit(
            ActivateAbility(
                d.player1,
                kami,
                returnAbility,
                targets = listOf(ChosenTarget.Card(bears, d.player1, Zone.GRAVEYARD)),
            )
        )

        withClue("a filter that ignored the Arcane subtype would accept this and return the Bears") {
            result.isSuccess shouldBe false
            d.getGraveyardCardNames(d.player1) shouldContain "Grizzly Bears"
        }
    }
})
