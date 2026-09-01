package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HoofprintsOfTheStag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Hoofprints of the Stag (LRW #21) — "Whenever you draw a card, you may put a hoofprint counter on
 * this enchantment. {2}{W}, Remove four hoofprint counters from this enchantment: Create a 4/4
 * white Elemental creature token with flying. Activate only during your turn."
 *
 * `hoofprint` is a new counter type, and the failure mode of getting that wrong is silent: with no
 * `CounterType` enum entry, `resolveCounterType` falls back to **+1/+1** and every read counts the
 * wrong thing while nothing errors. So the tests read the tally back by
 * `CounterType.HOOFPRINT` explicitly rather than trusting the ability to be self-consistent.
 *
 * The 2007-10-01 ruling — a multi-card draw triggers it that many times — is the other half, and it
 * is the shape that separates a per-card trigger from a per-*event* one.
 */
class HoofprintsOfTheStagScenarioTest : FunSpec({

    val tokenAbility = HoofprintsOfTheStag.activatedAbilities.single().id

    // A plain "draw three" so the per-card trigger count can be observed from one spell.
    val drawThree = card("Test Draw Three") {
        manaCost = "{2}{U}"
        colorIdentity = "U"
        typeLine = "Sorcery"
        spell { effect = Effects.DrawCards(3) }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + HoofprintsOfTheStag)
        d.registerCard(drawThree)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.hoofprints(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.HOOFPRINT) ?: 0

    /** Answer every pending "you may" with [yes] and let the stack drain. */
    fun settle(d: GameTestDriver, player: EntityId, yes: Boolean) {
        var guard = 0
        while (guard++ < 20) {
            when {
                d.isPaused -> d.submitYesNo(player, yes)
                d.stackSize > 0 -> d.bothPass()
                else -> return
            }
        }
    }

    /**
     * An ability whose cost can't be met is still *listed* — greyed out, `affordable = false` — so
     * the client can show why it's dead. "Offered" therefore means an affordable row.
     */
    fun canActivate(d: GameTestDriver, player: EntityId, source: EntityId): Boolean =
        d.legalActions(player).any {
            (it.action as? ActivateAbility)?.sourceId == source && it.affordable
        }

    test("drawing three cards triggers it three times, and each 'may' is answered on its own") {
        val d = driver()
        val me = d.activePlayer!!

        val enchantment = d.putPermanentOnBattlefield(me, "Hoofprints of the Stag")
        val spell = d.putCardInHand(me, "Test Draw Three")
        d.giveMana(me, Color.BLUE, 3)

        d.castSpell(me, spell).isSuccess shouldBe true
        settle(d, me, yes = true)

        withClue("one trigger per card drawn (2007-10-01 ruling)") {
            d.hoofprints(enchantment) shouldBe 3
        }
    }

    test("declining the trigger puts no counter on") {
        val d = driver()
        val me = d.activePlayer!!

        val enchantment = d.putPermanentOnBattlefield(me, "Hoofprints of the Stag")
        val spell = d.putCardInHand(me, "Test Draw Three")
        d.giveMana(me, Color.BLUE, 3)

        d.castSpell(me, spell).isSuccess shouldBe true
        settle(d, me, yes = false)

        d.hoofprints(enchantment) shouldBe 0
    }

    test("four hoofprint counters buy a 4/4 white flier, and are spent doing it") {
        val d = driver()
        val me = d.activePlayer!!

        val enchantment = d.putPermanentOnBattlefield(me, "Hoofprints of the Stag")
        d.addComponent(enchantment, CountersComponent(mapOf(CounterType.HOOFPRINT to 4)))
        d.giveMana(me, Color.WHITE, 3)

        d.submit(ActivateAbility(me, enchantment, tokenAbility)).isSuccess shouldBe true
        d.bothPass()

        val token = d.getCreatures(me).singleOrNull().shouldNotBeNull()
        d.state.projectedState.getPower(token) shouldBe 4
        d.state.projectedState.getToughness(token) shouldBe 4
        d.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
        withClue("the four counters were the cost") {
            d.hoofprints(enchantment) shouldBe 0
        }
    }

    test("three counters aren't enough — the ability isn't offered and can't be paid") {
        val d = driver()
        val me = d.activePlayer!!

        val enchantment = d.putPermanentOnBattlefield(me, "Hoofprints of the Stag")
        d.addComponent(enchantment, CountersComponent(mapOf(CounterType.HOOFPRINT to 3)))
        d.giveMana(me, Color.WHITE, 3)

        withClue("three of four counters leaves the ability greyed out, not payable") {
            canActivate(d, me, enchantment) shouldBe false
        }
        d.submitExpectFailure(ActivateAbility(me, enchantment, tokenAbility)).isSuccess shouldBe false
        d.getCreatures(me).isEmpty() shouldBe true
    }

    test("\"Activate only during your turn\" — not on the opponent's turn, even with the counters") {
        val d = driver()
        val me = d.activePlayer!!

        val enchantment = d.putPermanentOnBattlefield(me, "Hoofprints of the Stag")
        d.addComponent(enchantment, CountersComponent(mapOf(CounterType.HOOFPRINT to 4)))

        // Hand the turn over; the enchantment's controller is now the non-active player.
        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.activePlayer shouldBe d.getOpponent(me)
        d.giveMana(me, Color.WHITE, 3)

        canActivate(d, me, enchantment) shouldBe false
        d.submitExpectFailure(ActivateAbility(me, enchantment, tokenAbility)).isSuccess shouldBe false
    }
})
