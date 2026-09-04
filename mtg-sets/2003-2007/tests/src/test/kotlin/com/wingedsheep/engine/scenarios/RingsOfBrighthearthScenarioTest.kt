package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lea.cards.ProdigalSorcerer
import com.wingedsheep.mtg.sets.definitions.lrw.cards.RingsOfBrighthearth
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rings of Brighthearth (LRW #259) — "Whenever you activate an ability, if it isn't a mana ability,
 * you may pay {2}. If you do, copy that ability. You may choose new targets for the copy."
 *
 * Three axes, and each is a place the card would read right and resolve wrong:
 *
 *  - **The copy has to find the ability that fired it.** `EffectTarget.TriggeringEntity` on an
 *    `AbilityActivatedEvent` is the activated ability still on the stack beneath this trigger; if it
 *    resolved to nothing, the whole card would be a silent no-op that the snapshot golden cannot
 *    see. The happy path asserts *two* pings landed, not just that a decision was offered.
 *  - **The {2} is optional.** Declining must leave the original ability untouched — one ping, not
 *    zero and not two.
 *  - **A mana ability must not trigger it at all.** This is the clause that is easiest to wire
 *    twice or not at all; Llanowar Elves' "{T}: Add {G}" is the cheapest proof, and the assertion is
 *    that no decision is ever raised.
 *
 * The copy's "you may choose new targets" prompt is a `ChooseTargetsDecision` raised by the shared
 * copy executor (CR 707.10c), so the happy path re-submits the same target rather than skipping it.
 */
class RingsOfBrighthearthScenarioTest : FunSpec({

    val pingAbility = ProdigalSorcerer.activatedAbilities.single().id
    val elvesMana = TestCards.LlanowarElves.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + ProdigalSorcerer + RingsOfBrighthearth)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Rings out, a ready pinger, and {2} floating to pay the optional cost with. */
    fun GameTestDriver.setUpPinger(): EntityId {
        putPermanentOnBattlefield(player1, "Rings of Brighthearth")
        val tim = putCreatureOnBattlefield(player1, "Prodigal Sorcerer")
        removeSummoningSickness(tim)
        giveColorlessMana(player1, 2)
        return tim
    }

    test("paying {2} copies the activated ability — the opponent takes the ping twice") {
        val d = driver()
        val tim = d.setUpPinger()

        d.submit(
            ActivateAbility(d.player1, tim, pingAbility, targets = listOf(ChosenTarget.Player(d.player2)))
        ).isSuccess shouldBe true

        withClue("the Rings trigger goes on the stack above the ping it copies") {
            d.pendingDecision shouldBe null
        }
        d.bothPass()

        withClue("the trigger resolves first and asks its controller for the {2}") {
            d.pendingDecision?.playerId shouldBe d.player1
        }
        d.submitYesNo(d.player1, true)

        // The copy offers new targets (CR 707.10c); keep the opponent.
        if (d.pendingDecision != null) {
            d.submitTargetSelection(d.player1, listOf(d.player2))
        }
        d.bothPass()
        d.bothPass()

        withClue("the copy and the original each deal 1 — 20 - 2 = 18") {
            d.getLifeTotal(d.player2) shouldBe 18
        }
    }

    test("declining the {2} leaves the original ability alone — one ping, not two") {
        val d = driver()
        val tim = d.setUpPinger()

        d.submit(
            ActivateAbility(d.player1, tim, pingAbility, targets = listOf(ChosenTarget.Player(d.player2)))
        ).isSuccess shouldBe true
        d.bothPass()
        d.submitYesNo(d.player1, false)
        d.bothPass()

        withClue("no copy was made, but the ability that triggered it still resolves") {
            d.getLifeTotal(d.player2) shouldBe 19
        }
    }

    test("a mana ability never triggers it — no {2} is ever offered") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player1, "Rings of Brighthearth")
        val elves = d.putCreatureOnBattlefield(d.player1, "Llanowar Elves")
        d.removeSummoningSickness(elves)
        d.giveColorlessMana(d.player1, 2)

        d.submit(ActivateAbility(d.player1, elves, elvesMana)).isSuccess shouldBe true

        withClue("a mana ability doesn't use the stack, so there is nothing to copy and nothing to ask") {
            d.pendingDecision shouldBe null
            d.state.stack.isEmpty() shouldBe true
        }
    }
})
