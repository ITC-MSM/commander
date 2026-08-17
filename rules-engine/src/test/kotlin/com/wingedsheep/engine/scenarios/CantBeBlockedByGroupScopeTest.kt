package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * `CantBeBlockedBy(blockerFilter, filter)` with a **battlefield-scoped** `GroupFilter` — the shape
 * behind "creatures you control can't be blocked by X" (Wall Crawl's Spiders; the "or block
 * creatures you control" half of Storm, Windrider).
 *
 * `CantBeBlockedByRule` reads three sources for the attacker's restrictions: its own printed
 * self-scoped statics, granted ones, and *host-scoped* ones projected from another battlefield
 * permanent through the group filter. That third scan used to skip the host whenever the host was
 * itself the attacker, which silently exempted a **creature** whose own group clause names a group
 * it belongs to — Storm is a "creature you control". The host is now included, with `excludeSelf`
 * on the group filter honored for the "other creatures you control …" wording.
 */
class CantBeBlockedByGroupScopeTest : FunSpec({

    /** "Creatures you control can't be blocked by creatures with flying." — host is in the group. */
    val skyDenier = card("Test Sky Denier") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Elemental"
        power = 3
        toughness = 3
        oracleText = "Creatures you control can't be blocked by creatures with flying."
        staticAbility {
            ability = CantBeBlockedBy(
                blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
                filter = GroupFilter(GameObjectFilter.Creature.youControl())
            )
        }
    }

    /** The "other creatures you control" wording — the host must stay out of its own group. */
    val otherSkyDenier = card("Test Other Sky Denier") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Elemental"
        power = 3
        toughness = 3
        oracleText = "Other creatures you control can't be blocked by creatures with flying."
        staticAbility {
            ability = CantBeBlockedBy(
                blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
                filter = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true)
            )
        }
    }

    val testFlier = card("Test Group Flier") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Bird"
        power = 1
        toughness = 3
        keywords(Keyword.FLYING)
    }

    val testGrounder = card("Test Group Grounder") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Beast"
        power = 1
        toughness = 3
    }

    val extras = listOf(skyDenier, otherSkyDenier, testFlier, testGrounder)

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the group clause covers another creature its controller controls") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)

        d.putCreatureOnBattlefield(attacker, "Test Sky Denier")
        val grunt = d.putCreatureOnBattlefield(attacker, "Test Group Grounder")
        d.removeSummoningSickness(grunt)
        val flier = d.putCreatureOnBattlefield(defender, "Test Group Flier")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(grunt), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        withClue("a flier can't block a creature covered by the group clause") {
            d.declareBlockers(defender, mapOf(flier to listOf(grunt))).error shouldNotBe null
        }
    }

    test("the group clause covers the permanent granting it when that permanent attacks") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)

        val denier = d.putCreatureOnBattlefield(attacker, "Test Sky Denier")
        d.removeSummoningSickness(denier)
        val flier = d.putCreatureOnBattlefield(defender, "Test Group Flier")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(denier), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        withClue("the host is itself a \"creature you control\"") {
            d.declareBlockers(defender, mapOf(flier to listOf(denier))).error shouldNotBe null
        }
    }

    test("excludeSelf keeps the granting permanent out of its own group") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)

        val denier = d.putCreatureOnBattlefield(attacker, "Test Other Sky Denier")
        d.removeSummoningSickness(denier)
        val flier = d.putCreatureOnBattlefield(defender, "Test Group Flier")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(denier), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        withClue("\"other creatures you control\" does not cover the host") {
            d.declareBlockers(defender, mapOf(flier to listOf(denier))).error shouldBe null
        }
    }

    test("a non-flying blocker is unaffected by the clause") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)

        val denier = d.putCreatureOnBattlefield(attacker, "Test Sky Denier")
        d.removeSummoningSickness(denier)
        val grounder = d.putCreatureOnBattlefield(defender, "Test Group Grounder")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(denier), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(defender, mapOf(grounder to listOf(denier))).error shouldBe null
    }

    test("the clause does not reach creatures the opposing player controls") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)

        // The *defender* controls the denier, so its clause covers the defender's creatures —
        // never the attacking player's, whose attacker must stay blockable by a flier.
        d.putCreatureOnBattlefield(defender, "Test Sky Denier")
        val grunt = d.putCreatureOnBattlefield(attacker, "Test Group Grounder")
        d.removeSummoningSickness(grunt)
        val flier = d.putCreatureOnBattlefield(defender, "Test Group Flier")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(grunt), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(defender, mapOf(flier to listOf(grunt))).error shouldBe null
    }
})
