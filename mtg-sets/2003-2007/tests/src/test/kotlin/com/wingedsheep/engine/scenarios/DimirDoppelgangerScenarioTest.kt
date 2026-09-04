package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.DimirDoppelganger
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dimir Doppelganger (RAV #202) — "{1}{U}{B}: Exile target creature card from a graveyard. This
 * creature becomes a copy of that card, except it has this ability."
 *
 * The "except it has this ability" clause is the whole test. The copy replaces the permanent's card
 * component wholesale, so without the re-grant the Doppelganger could copy exactly once and would
 * then be a plain creature. The 2005-10-01 ruling — "if it becomes a copy of a different creature
 * card, the new copy will overwrite the old copy" — is that same property from the other side, so
 * the *second* activation is what actually proves it.
 *
 * Also pinned: the target is a card in *a* graveyard, not only yours.
 */
class DimirDoppelgangerScenarioTest : FunSpec({

    val copyAbility = DimirDoppelganger.activatedAbilities[0].id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + DimirDoppelganger)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Activates the Doppelganger's ability at [graveyardCard] and resolves it. */
    fun GameTestDriver.copyCard(
        controller: EntityId,
        doppelganger: EntityId,
        graveyardCard: EntityId,
        cardOwner: EntityId,
    ) {
        giveMana(controller, Color.BLUE, 1)
        giveMana(controller, Color.BLACK, 1)
        giveColorlessMana(controller, 1)
        val result = submit(
            ActivateAbility(
                playerId = controller,
                sourceId = doppelganger,
                abilityId = copyAbility,
                targets = listOf(ChosenTarget.Card(graveyardCard, cardOwner, Zone.GRAVEYARD)),
            )
        )
        withClue(result.error ?: "activation failed") { result.isSuccess shouldBe true }
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("becomes a copy of the exiled card and keeps its own ability, so it can copy again") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        val doppel = d.putCreatureOnBattlefield(me, "Dimir Doppelganger")
        d.removeSummoningSickness(doppel)
        // One card in each graveyard — "a graveyard" is not "your graveyard".
        val lions = d.putCardInGraveyard(me, "Savannah Lions")
        val warrior = d.putCardInGraveyard(opp, "Phantom Warrior")

        d.copyCard(me, doppel, lions, me)

        withClue("the copy source is exiled") {
            d.getExileCardNames(me).contains("Savannah Lions") shouldBe true
        }
        withClue("the permanent now reads as the copied card") {
            d.getCardName(doppel) shouldBe "Savannah Lions"
        }

        // The re-grant is what makes this next activation possible at all.
        d.copyCard(me, doppel, warrior, opp)

        withClue("a card in an opponent's graveyard is a legal source") {
            d.getExileCardNames(opp).contains("Phantom Warrior") shouldBe true
        }
        withClue("the new copy overwrites the old one (2005-10-01 ruling)") {
            d.getCardName(doppel) shouldBe "Phantom Warrior"
        }
    }
})
