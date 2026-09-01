package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.CeaselessSearblades
import com.wingedsheep.mtg.sets.definitions.lrw.cards.GoldmeadowHarrier
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HoofprintsOfTheStag
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Smokebraider
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ceaseless Searblades (LRW #158) — "Whenever you activate an ability of an Elemental, this
 * creature gets +1/+0 until end of turn."
 *
 * Every test here targets one of the three readings the line hides, because each of them has a
 * plausible wrong spelling that still looks right on the card:
 *
 * - "an Elemental" is the **bare tribal noun** — an Elemental *permanent*, not an Elemental
 *   creature. Hoofprints of the Stag is a Kindred Enchantment — Elemental, and activating it must
 *   fire this.
 * - "an ability" is **unqualified**, so a mana ability counts (`includeManaAbilities`).
 *   Smokebraider's `{T}: Add two mana …` is the case a default "isn't a mana ability" trigger
 *   would silently drop.
 * - A non-Elemental permanent's ability must do nothing at all.
 */
class CeaselessSearbladesScenarioTest : FunSpec({

    val hoofprintAbility = HoofprintsOfTheStag.activatedAbilities.single().id
    val harrierAbility = GoldmeadowHarrier.activatedAbilities.single().id
    val smokebraiderAbility = Smokebraider.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all + listOf(
                CeaselessSearblades, HoofprintsOfTheStag, GoldmeadowHarrier, Smokebraider
            )
        )
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.power(id: EntityId): Int = state.projectedState.getPower(id) ?: 0

    /** Let every pending decision and every stack object settle. */
    fun drain(d: GameTestDriver) {
        var guard = 0
        while (guard++ < 20) {
            when {
                d.isPaused -> d.autoResolveDecision()
                d.stackSize > 0 -> d.bothPass()
                else -> return
            }
        }
    }

    /**
     * Tap a Smokebraider for its two restricted mana. "In any combination of colors" prompts per
     * pip, so the colour decision is answered twice before the stack is let go — the idiom
     * [SmokebraiderScenarioTest] already uses.
     */
    fun tapSmokebraider(d: GameTestDriver, player: EntityId, smokebraider: EntityId) {
        d.submit(ActivateAbility(player, smokebraider, smokebraiderAbility))
        var guard = 0
        while (d.state.pendingDecision != null && guard++ < 4) {
            val decision = d.state.pendingDecision!!
            d.submitDecision(player, ColorChosenResponse(decision.id, Color.RED))
        }
        drain(d)
    }

    test("a noncreature Elemental's ability counts — the bare tribal noun means permanents") {
        val d = driver()
        val me = d.activePlayer!!

        val searblades = d.putCreatureOnBattlefield(me, "Ceaseless Searblades")
        val hoofprints = d.putPermanentOnBattlefield(me, "Hoofprints of the Stag")
        d.addComponent(hoofprints, CountersComponent(mapOf(CounterType.HOOFPRINT to 4)))
        d.giveMana(me, Color.WHITE, 3)

        d.power(searblades) shouldBe 2
        d.submit(ActivateAbility(me, hoofprints, hoofprintAbility)).isSuccess shouldBe true
        drain(d)

        withClue("Hoofprints of the Stag is a Kindred Enchantment — Elemental") {
            d.power(searblades) shouldBe 3
        }
    }

    test("a mana ability counts too — the wording carries no 'isn't a mana ability' clause") {
        val d = driver()
        val me = d.activePlayer!!

        val searblades = d.putCreatureOnBattlefield(me, "Ceaseless Searblades")
        val smokebraider = d.putCreatureOnBattlefield(me, "Smokebraider")
        d.removeSummoningSickness(smokebraider)

        tapSmokebraider(d, me, smokebraider)

        d.power(searblades) shouldBe 3
    }

    test("a non-Elemental permanent's ability does nothing") {
        val d = driver()
        val me = d.activePlayer!!

        val searblades = d.putCreatureOnBattlefield(me, "Ceaseless Searblades")
        val harrier = d.putCreatureOnBattlefield(me, "Goldmeadow Harrier")
        d.removeSummoningSickness(harrier)
        d.giveMana(me, Color.WHITE, 1)

        d.submit(
            ActivateAbility(
                me, harrier, harrierAbility,
                targets = listOf(ChosenTarget.Permanent(searblades))
            )
        ).isSuccess shouldBe true
        drain(d)

        withClue("Goldmeadow Harrier is a Kithkin Soldier") {
            d.power(searblades) shouldBe 2
        }
    }

    test("it stacks — two Elemental activations are two triggers") {
        val d = driver()
        val me = d.activePlayer!!

        val searblades = d.putCreatureOnBattlefield(me, "Ceaseless Searblades")
        val first = d.putCreatureOnBattlefield(me, "Smokebraider")
        val second = d.putCreatureOnBattlefield(me, "Smokebraider")
        d.removeSummoningSickness(first)
        d.removeSummoningSickness(second)

        tapSmokebraider(d, me, first)
        tapSmokebraider(d, me, second)

        d.power(searblades) shouldBe 4
    }
})
