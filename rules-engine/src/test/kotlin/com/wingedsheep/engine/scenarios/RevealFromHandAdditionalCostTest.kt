package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsRevealedEvent
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain as stringShouldContain

/**
 * The reveal-from-hand additional cost — `CostAtom.RevealFromHand` as an
 * [com.wingedsheep.sdk.scripting.AdditionalCost.Atom], on its own and as the non-mana leg of
 * [com.wingedsheep.sdk.scripting.AdditionalCost.OrPay].
 *
 * The mechanic behind Lorwyn's tribal cycle ("reveal an Elf card from your hand or pay {3}" —
 * Wren's Run Vanquisher, Silvergill Adept, Goldmeadow Stalwart, Squeaking Pie Sneak, Flamekin
 * Bladewhirl). Two rules do the work and both are pinned here:
 *
 *  - **CR 701.20b** — revealing a card doesn't cause it to leave the zone it's in. Paying moves
 *    nothing; the revealed card is still in hand and still castable afterwards.
 *  - **CR 701.4a** — "behold a [quality]" is "reveal a [quality] card from your hand **or** choose
 *    a [quality] permanent you control". Behold is the strictly *wider* action, which is why this
 *    cost has its own [AdditionalCostPayment.revealedCards] channel rather than sharing behold's:
 *    a battlefield permanent can never pay a hand-only reveal, and on the OrPay leg the payment
 *    field is the only thing telling the engine which leg the caster took.
 */
class RevealFromHandAdditionalCostTest : ScenarioTestBase() {

    init {
        // "Reveal an Elf card from your hand or pay {3}" — the printed Lorwyn shape.
        val revealOrPay = card("Elf Herald") {
            manaCost = "{1}{G}"
            colorIdentity = "G"
            typeLine = "Creature — Elf Warrior"
            power = 3
            toughness = 3
            oracleText = "As an additional cost to cast this spell, reveal an Elf card from your hand or pay {3}."
            additionalCost(
                Costs.additional.RevealFromHandOrPay(
                    filter = GameObjectFilter.Any.withSubtype(Subtype.ELF),
                    alternativeManaCost = "{3}"
                )
            )
        }
        // The same cost without the mana escape hatch: a mandatory reveal.
        val mandatoryReveal = card("Elf Crier") {
            manaCost = "{G}"
            colorIdentity = "G"
            typeLine = "Creature — Elf Scout"
            power = 1
            toughness = 1
            oracleText = "As an additional cost to cast this spell, reveal an Elf card from your hand."
            additionalCost(
                Costs.additional.RevealFromHand(
                    filter = GameObjectFilter.Any.withSubtype(Subtype.ELF)
                )
            )
        }
        // A Kindred *noncreature* Elf: an "Elf card" for the filter, but never a creature — the
        // shape that separates "an Elf card" from "an Elf creature card".
        val kindredElf = card("Elf Rite") {
            manaCost = "{G}"
            colorIdentity = "G"
            typeLine = "Kindred Instant — Elf"
            oracleText = "Draw a card."
            spell {
                effect = com.wingedsheep.sdk.dsl.Effects.DrawCards(1)
            }
        }
        // A permanent Elf, to prove a *battlefield* Elf can't pay a hand-only reveal (CR 701.4a).
        val elfOnBoard = card("Elf Sentry") {
            manaCost = "{G}"
            colorIdentity = "G"
            typeLine = "Creature — Elf Soldier"
            power = 1
            toughness = 1
        }
        val nonElf = card("Plain Bear") {
            manaCost = "{1}{G}"
            colorIdentity = "G"
            typeLine = "Creature — Bear"
            power = 2
            toughness = 2
        }
        listOf(revealOrPay, mandatoryReveal, kindredElf, elfOnBoard, nonElf).forEach { cardRegistry.register(it) }

        fun TestGame.handCardNamed(name: String): EntityId =
            state.getHand(player1Id).first { state.getEntity(it)?.get<CardComponent>()?.name == name }

        fun TestGame.tappedForests(): Int = state.getBattlefield(player1Id).count { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == "Forest" &&
                state.getEntity(id)?.has<TappedComponent>() == true
        }

        fun board(
            forests: Int,
            handExtras: List<String>,
            elfOnBattlefield: Boolean = false,
        ): TestGame {
            var builder = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Elf Herald")
                .withLandsOnBattlefield(1, "Forest", forests)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            handExtras.forEach { builder = builder.withCardInHand(1, it) }
            if (elfOnBattlefield) builder = builder.withCardOnBattlefield(1, "Elf Sentry")
            repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
            return builder.build()
        }

        context("the OrPay leg") {

            test("both the reveal path and the pay path are enumerated when the hand has an Elf and mana for either") {
                // Five Forests: enough for the base {1}{G} (2) and for {1}{G} + {3} (5).
                val game = board(forests = 5, handExtras = listOf("Elf Sentry"))
                val herald = game.handCardNamed("Elf Herald")

                val casts = game.getLegalActions(1).filter { (it.action as? CastSpell)?.cardId == herald }
                withClue("one action per leg: reveal (base cost) and pay (base + {3})") {
                    casts.size shouldBe 2
                }
                val revealLeg = casts.firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
                revealLeg.shouldNotBeNull()
                withClue("the reveal picker's pool is the Elf in hand, not the spell being cast") {
                    revealLeg.additionalCostInfo!!.validRevealTargets shouldBe
                        listOf(game.handCardNamed("Elf Sentry"))
                    revealLeg.additionalCostInfo!!.revealCount shouldBe 1
                }
            }

            test("CR 701.20b — the revealed Elf stays in hand and can still be cast afterwards") {
                val game = board(forests = 3, handExtras = listOf("Elf Sentry"))
                val herald = game.handCardNamed("Elf Herald")
                val elf = game.handCardNamed("Elf Sentry")

                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = herald,
                        additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(elf)),
                    )
                )
                result.error shouldBe null
                withClue("the reveal is published as an event so the opponent sees it") {
                    val revealed = result.events.filterIsInstance<CardsRevealedEvent>().firstOrNull()
                    revealed.shouldNotBeNull()
                    revealed.cardIds shouldContain elf
                    revealed.cardNames shouldContain "Elf Sentry"
                }
                withClue("revealing moves nothing — the Elf is still in hand") {
                    game.state.getZone(game.player1Id, Zone.HAND) shouldContain elf
                }
                withClue("only the base {1}{G} was charged, not {1}{G} + {3}") {
                    game.tappedForests() shouldBe 2
                }
                game.resolveStack()
                game.findPermanent("Elf Herald") shouldNotBe null
                withClue("the revealed Elf is untouched and still castable") {
                    game.castSpell(1, "Elf Sentry").error shouldBe null
                }
            }

            test("the pay path costs {3} more and reveals nothing") {
                val game = board(forests = 5, handExtras = listOf("Elf Sentry"))
                val herald = game.handCardNamed("Elf Herald")

                val result = game.execute(CastSpell(playerId = game.player1Id, cardId = herald))
                result.error shouldBe null
                withClue("no reveal happened on the pay path") {
                    result.events.filterIsInstance<CardsRevealedEvent>().isEmpty().shouldBeTrue()
                }
                withClue("{1}{G} + {3} taps five Forests") {
                    game.tappedForests() shouldBe 5
                }
            }

            test("with no Elf in hand only the pay path is offered, and the spell is still castable") {
                val game = board(forests = 5, handExtras = listOf("Plain Bear"))
                val herald = game.handCardNamed("Elf Herald")

                val casts = game.getLegalActions(1).filter { (it.action as? CastSpell)?.cardId == herald }
                withClue("nothing to reveal, so the leg path is declined and only the pay path remains") {
                    casts.size shouldBe 1
                    casts.single().additionalCostInfo?.costType shouldNotBe "RevealCard"
                }
                game.execute(casts.single().action).error shouldBe null
                game.tappedForests() shouldBe 5
            }

            test("CR 701.4a — an Elf on the battlefield is not an Elf card in hand and cannot pay the reveal") {
                // Behold would accept this permanent; a reveal-from-hand must not.
                val game = board(forests = 5, handExtras = listOf("Plain Bear"), elfOnBattlefield = true)
                val herald = game.handCardNamed("Elf Herald")
                val elfPermanent = game.findPermanent("Elf Sentry")
                elfPermanent.shouldNotBeNull()

                val casts = game.getLegalActions(1).filter { (it.action as? CastSpell)?.cardId == herald }
                withClue("the battlefield Elf never enters the reveal pool") {
                    casts.none { it.additionalCostInfo?.costType == "RevealCard" }.shouldBeTrue()
                }
                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = herald,
                        additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(elfPermanent)),
                    )
                )
                withClue("submitting it anyway is rejected") {
                    result.error.shouldNotBeNull()
                    result.error!! stringShouldContain "not in your hand"
                }
            }

            test("a Kindred noncreature Elf card pays it — the filter is 'an Elf card', not 'an Elf creature card'") {
                val game = board(forests = 3, handExtras = listOf("Elf Rite"))
                val herald = game.handCardNamed("Elf Herald")
                val rite = game.handCardNamed("Elf Rite")

                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = herald,
                        additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(rite)),
                    )
                )
                result.error shouldBe null
                game.tappedForests() shouldBe 2
            }

            test("a non-Elf card in hand is rejected as the reveal") {
                val game = board(forests = 5, handExtras = listOf("Plain Bear"))
                val herald = game.handCardNamed("Elf Herald")
                val bear = game.handCardNamed("Plain Bear")

                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = herald,
                        additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(bear)),
                    )
                )
                withClue("the filter is enforced at validation, not just in the picker's pool") {
                    result.error.shouldNotBeNull()
                    result.error!! stringShouldContain "doesn't match"
                }
            }
        }

        context("the bare cost, with no mana alternative") {

            fun crierBoard(handExtras: List<String>): TestGame {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Elf Crier")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                handExtras.forEach { builder = builder.withCardInHand(1, it) }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                return builder.build()
            }

            test("the cost is offered with its own picker when the hand holds a matching card") {
                val game = crierBoard(listOf("Elf Sentry"))
                val crier = game.handCardNamed("Elf Crier")

                val cast = game.getLegalActions(1).firstOrNull { (it.action as? CastSpell)?.cardId == crier }
                cast.shouldNotBeNull()
                cast.additionalCostInfo?.costType shouldBe "RevealCard"
                cast.additionalCostInfo!!.validRevealTargets shouldBe listOf(game.handCardNamed("Elf Sentry"))
            }

            test("the spell is uncastable with nothing to reveal — a mandatory cost fails closed") {
                val game = crierBoard(listOf("Plain Bear"))
                val crier = game.handCardNamed("Elf Crier")

                withClue("no Elf card in hand means the cost cannot be paid at all (CR 601.2f-h)") {
                    game.getLegalActions(1).none { (it.action as? CastSpell)?.cardId == crier }.shouldBeTrue()
                }
            }

            test("the spell can't pay the cost with itself") {
                // Elf Crier is itself an Elf card, but it is on its way to the stack — CR 601.2a
                // moves it there before costs are paid, so it is never in its own candidate pool.
                val game = crierBoard(emptyList())
                val crier = game.handCardNamed("Elf Crier")

                game.getLegalActions(1).none { (it.action as? CastSpell)?.cardId == crier }.shouldBeTrue()
            }
        }
    }
}
