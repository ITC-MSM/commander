package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.vow.cards.DollhouseOfHorrors
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dollhouse of Horrors (VOW #255).
 *
 * "{1}, {T}, Exile a creature card from your graveyard: Create a token that's a copy of the exiled
 * card, except it's a 0/0 Construct artifact in addition to its other types and it has 'This token
 * gets +1/+1 for each Construct you control.' It gains haste until end of turn."
 *
 * The exile is a **cost**, so by resolution the card is already in exile and the copy has to be read
 * off `CardSource.ExiledAsCost`. What is proved here: the token is a copy of the *paid* card, its
 * base P/T is overridden to 0/0 regardless of the copied card's printed stats, it picks up Construct
 * and artifact on top of the copied types, and the granted static counts Constructs — including
 * itself, per the first ruling.
 */
class DollhouseOfHorrorsScenarioTest : FunSpec({

    val abilityId = DollhouseOfHorrors.activatedAbilities.first().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(DollhouseOfHorrors)
        return d
    }

    fun tokenOf(d: GameTestDriver, playerId: EntityId, name: String, excluding: Set<EntityId>): EntityId =
        d.getPermanents(playerId)
            .firstOrNull { it !in excluding && d.getCardName(it) == name }
            ?: error(
                "no new permanent named '" + name + "'; battlefield now: " +
                    d.getPermanents(playerId).joinToString {
                        d.getCardName(it) + (if (it in excluding) " (pre-existing)" else " (NEW)")
                    }
            )

    test("the token copies the exiled card but enters as a 0/0 Construct artifact") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dollhouse = d.putPermanentOnBattlefield(me, "Dollhouse of Horrors")
        // Centaur Courser is a printed 3/3.
        val fuel = d.putCardInGraveyard(me, "Centaur Courser")
        d.giveColorlessMana(me, 1)

        val before = d.getPermanents(me).toSet()
        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = dollhouse,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(exiledCards = listOf(fuel)),
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("the cost exiled the creature card") {
            d.getExileCardNames(me) shouldBe listOf("Centaur Courser")
            d.getGraveyardCardNames(me) shouldBe emptyList()
        }

        val token = tokenOf(d, me, "Centaur Courser", before)
        val projected = d.state.projectedState

        withClue("0/0 base plus +1/+1 for the one Construct it is itself") {
            projected.getPower(token) shouldBe 1
            projected.getToughness(token) shouldBe 1
        }
        withClue("Construct on top of the copied Centaur subtype") {
            projected.hasSubtype(token, "Construct") shouldBe true
            projected.hasSubtype(token, "Centaur") shouldBe true
        }
        withClue("artifact in addition to its other types") {
            projected.hasType(token, "ARTIFACT") shouldBe true
            projected.isCreature(token) shouldBe true
        }
    }

    test("a second Construct token makes both tokens 2/2 — the token counts itself") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val first = d.putPermanentOnBattlefield(me, "Dollhouse of Horrors")
        val second = d.putPermanentOnBattlefield(me, "Dollhouse of Horrors")
        val fuelA = d.putCardInGraveyard(me, "Centaur Courser")
        val fuelB = d.putCardInGraveyard(me, "Grizzly Bears")
        d.giveColorlessMana(me, 2)

        val beforeA = d.getPermanents(me).toSet()
        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = first,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(exiledCards = listOf(fuelA)),
            )
        ).isSuccess shouldBe true
        d.bothPass()
        val tokenA = tokenOf(d, me, "Centaur Courser", beforeA)

        val beforeB = d.getPermanents(me).toSet()
        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = second,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(exiledCards = listOf(fuelB)),
            )
        ).isSuccess shouldBe true
        d.bothPass()
        val tokenB = tokenOf(d, me, "Grizzly Bears", beforeB)

        withClue("two Constructs on the battlefield, so each token is 0/0 +2/+2") {
            d.state.projectedState.getPower(tokenA) shouldBe 2
            d.state.projectedState.getToughness(tokenA) shouldBe 2
            d.state.projectedState.getPower(tokenB) shouldBe 2
            d.state.projectedState.getToughness(tokenB) shouldBe 2
        }
    }
})
