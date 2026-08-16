package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.conditions.NotCondition
import com.wingedsheep.sdk.scripting.conditions.YouWereAttackedThisStep
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.dsl.Conditions as SdkConditions

/**
 * The state tests a card's text names — the "if …" half of a conditional clause and the "only if …"
 * half of a cast restriction.
 *
 * The family opens with one member, which is what a condition family looks like the first time a
 * card needs one. Every rule builds through the SDK's `Conditions` facade rather than assembling a
 * `Compare` by hand: those facades are the curated surface, and a condition assembled here would be
 * a second spelling of one the SDK already names — the ambiguity this module refuses everywhere
 * else.
 *
 * That is also the honest reason the list is short. A condition in Oracle text is an ordinary
 * English clause with a vocabulary as large as the game's, and the SDK names only the ones cards
 * have needed; a rule per named condition is the shape that keeps the two in step, and a printed
 * condition the SDK cannot name declines and is counted.
 */
object Conditions {

    /**
     * The battlefield-existence conditions — "you control a Beast", "no opponent controls a
     * creature".
     *
     * One shape with a noun-phrase slot rather than a constant per tribe, which is what
     * `Conditions.YouControl` / `Conditions.OpponentControls` being *general* facades buys: the
     * whole of [Filters] arrives, so "you control a Beast" and "you control an artifact" are the
     * same rule. The article comes from [Filters.indefinite], which owns it in both directions.
     *
     * The negation is `Conditions.Not` around the positive rather than the `negate` flag the same
     * facades take, because those are two different values and the hand-written cards use the
     * wrapper — Vexing Beetle's `Not(OpponentControlsCreature)`. Emitting the flag would round-trip
     * and disagree with every card that spells it.
     */
    private fun existence(
        template: String,
        name: String,
        condition: (GameObjectFilter) -> Condition,
    ): Phrase<Condition> = phrase(template, name = name) {
        slot("filter", Filters.indefinite)
        build { condition(it.value("filter")) }
        match { value ->
            val filter = existenceFilter(value) ?: return@match null
            if (value != condition(filter)) return@match null
            bind("filter" to filter)
        }
    }

    /**
     * The filter an existence condition scans for, looking through the negation wrapper.
     *
     * A candidate only: the reconstruction in [existence] decides whether the whole condition is
     * this sentence, so nothing here has to check the player or the zone.
     */
    private fun existenceFilter(condition: Condition): GameObjectFilter? = when (condition) {
        is Exists -> condition.filter
        is NotCondition -> existenceFilter(condition.condition)
        else -> null
    }

    val all: List<Phrase<Condition>> = listOf(
        constant("an opponent controls more lands than you", SdkConditions.OpponentControlsMoreLands),
        // `YouWereAttackedThisStep` has no facade entry — it is a `data object` cards reference
        // directly, the same situation `Replacements` and the combat statics are in. Reported as the
        // small SDK finding it is rather than routed around.
        constant("you've been attacked this step", YouWereAttackedThisStep),
        // "When ~ enters, if you didn't cast it from your hand, …" — Phage the Untouchable. The
        // pronoun is the source and the whole clause is one named SDK condition, so there is nothing
        // to slot.
        constant("you didn't cast it from your hand", SdkConditions.Not(SdkConditions.WasCastFromHand)),
        existence("you control {filter}", "you control a permanent") { SdkConditions.YouControl(it) },
        existence("an opponent controls {filter}", "an opponent controls a permanent") {
            SdkConditions.OpponentControls(it)
        },
        existence("no opponent controls {filter}", "no opponent controls a permanent") {
            SdkConditions.Not(SdkConditions.OpponentControls(it))
        },
        existence("an opponent controls no {filter}", "an opponent controls none of a permanent") {
            SdkConditions.OpponentControls(it, negate = true)
        },
        // "This spell costs {2} less to cast if it's bargained." — Hamlet Glutton. The SDK reads the
        // durable cast-choice slot rather than naming a condition per mechanic, and `WasBargained`
        // is the facade over exactly that read, so the rule is a constant and the mechanic's other
        // spellings arrive as sibling rows rather than as a shape.
        constant("it's bargained", SdkConditions.WasBargained),
        constant("it's kicked", SdkConditions.WasKicked),
        countAtLeast(
            "you control {n} or more {filter}",
            "you control several permanents",
        ) { count, filter -> SdkConditions.YouControlAtLeast(count, filter) },
        countAtLeast(
            "there are {n} or more {filter} cards in your graveyard",
            "several cards of a kind in your graveyard",
        ) { count, filter -> SdkConditions.CardsInGraveyardMatchingAtLeast(count, filter) },
        // Lavaborn Muse's intervening-if. "That player" is the one whose step triggered, which the
        // SDK names as `Player.TriggeringPlayer`.
        zoneCount(
            "that player has {n} or fewer cards in hand",
            "the triggering player's hand size",
            Player.TriggeringPlayer,
            Zone.HAND,
            ComparisonOperator.LTE,
        ),
        // Threshold's own condition, and the second member of the shape above rather than a second
        // rule: both count one zone for one player against a spelled number, and the two differ only
        // in which zone, whose, and which way the comparison points.
        zoneCount(
            "there are {n} or more cards in your graveyard",
            "your graveyard's size",
            Player.You,
            Zone.GRAVEYARD,
            ComparisonOperator.GTE,
        ),
    )

    /**
     * "There are seven or more cards in your graveyard", "that player has two or fewer cards in
     * hand" — a zone's card count against a number the text spells.
     *
     * Written as a `Compare` rather than through a facade because `Conditions` publishes no
     * zone-count entry — the same situation [Replacements] and the combat statics are in, and
     * reported as the small SDK finding it is rather than routed around.
     */
    private fun zoneCount(
        template: String,
        name: String,
        player: Player,
        zone: Zone,
        operator: ComparisonOperator,
    ): Phrase<Condition> {
        fun conditionFor(limit: Int): Condition =
            Compare(DynamicAmount.Count(player, zone), operator, DynamicAmount.Fixed(limit))
        return phrase(template, name = name) {
            slot("n", Cardinals.word)
            build { conditionFor(it.int("n")) }
            match { value ->
                val compare = value as? Compare ?: return@match null
                val limit = (compare.right as? DynamicAmount.Fixed)?.amount ?: return@match null
                if (!Cardinals.spellable(limit) || value != conditionFor(limit)) return@match null
                bind("n" to limit)
            }
        }
    }

    /**
     * "You control two or more legendary creatures", "there are two or more creature cards in your
     * graveyard" — a *counted* group against a spelled number, where [existence] asks only whether
     * one exists.
     *
     * The two shapes stay disjoint by construction rather than by convention. [existence] builds an
     * `Exists` and this builds a `Compare`, so no value is reachable from both; and the number comes
     * from [Cardinals.word], which starts at two, so "you control a Wizard" cannot also be read as a
     * count of one. That is the same disjoint-domains fix `drawOne` and `Cardinals.word` use, and it
     * is what keeps the printer from having to choose.
     *
     * The filter must narrow something. `GameObjectFilter.Any` would print as a bare noun the
     * unfiltered rules already spell, which is one model with two printed forms — so an unnarrowed
     * count declines here and is counted, rather than being spelled a second way.
     */
    private fun countAtLeast(
        template: String,
        name: String,
        condition: (Int, GameObjectFilter) -> Condition,
    ): Phrase<Condition> = phrase(template, name = name) {
        slot("n", Cardinals.word)
        slot("filter", Filters.plural)
        build { bindings ->
            val filter = bindings.value<GameObjectFilter>("filter")
            if (filter == GameObjectFilter.Any) return@build null
            condition(bindings.int("n"), filter)
        }
        match { value ->
            val compare = value as? Compare ?: return@match null
            val count = (compare.right as? DynamicAmount.Fixed)?.amount ?: return@match null
            val filter = countedFilter(compare.left) ?: return@match null
            if (filter == GameObjectFilter.Any || !Cardinals.spellable(count)) return@match null
            if (value != condition(count, filter)) return@match null
            bind("n" to count, "filter" to filter)
        }
    }

    /** The filter a counted amount narrows by — a candidate only; [countAtLeast] decides. */
    private fun countedFilter(amount: DynamicAmount): GameObjectFilter? = when (amount) {
        is DynamicAmount.AggregateBattlefield -> amount.filter
        is DynamicAmount.Count -> amount.filter
        else -> null
    }

    val condition: Phrase<Condition> = oneOf("a condition", all)
}
