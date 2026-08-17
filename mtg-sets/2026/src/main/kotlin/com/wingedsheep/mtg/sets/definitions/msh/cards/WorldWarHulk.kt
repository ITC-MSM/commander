package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * World War Hulk — Marvel Super Heroes #197
 * {3}{G}{G} · Enchantment — Saga · Rare
 *
 * I — The next red or green creature spell you cast this turn can be cast without paying its
 *     mana cost.
 * II — Put three +1/+1 counters on target creature you control.
 * III — Choose target creature you control. Until end of turn, double its power and toughness
 *       and it gains trample.
 *
 * Modeling notes:
 *  - Chapter I is the one new piece of vocabulary: [Effects.GrantNextSpellFreeCast], a one-shot
 *    pending rider on the game state in the same family as Mistrise Village's "next spell can't be
 *    countered" and Don & Raph's "next spell has affinity". The rider — not a battlefield static —
 *    is what the printed text describes: the permission has already resolved, so it survives the
 *    Saga being destroyed in response, and it applies to a spell cast from any zone. It is spent by
 *    the next matching spell *cast*, whether or not the free cast is taken; "the next red or green
 *    creature spell you cast this turn" names a spell, and a matching spell cast for full price is
 *    that spell. Per CR 118.9 the permission is an alternative cost, so mandatory additional costs
 *    still apply, and X is 0 (CR 107.3b) — the free-cast action the enumerator offers carries no X.
 *  - "Red or green creature spell" is a plain filter conjunction — a colour union ANDed with
 *    `Creature`, in that order so the rendered description reads "red or green creature".
 *  - Chapter II is a straight [Effects.AddCounters]; the target is chosen when the chapter ability
 *    triggers and goes on the stack, so a creature that leaves in response makes the chapter fizzle.
 *  - Chapter III is Epic Fight's doubling shape ([Effects.ModifyStats] with both halves read off
 *    the target at resolution — the standard layer-7c +N/+N modification) composed with a trample
 *    grant on the same target. Both halves are "until end of turn" and the bonus is locked in when
 *    applied, so it doesn't feed back on itself.
 */
val WorldWarHulk = card("World War Hulk") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — The next red or green creature spell you cast this turn can be cast without paying " +
        "its mana cost.\n" +
        "II — Put three +1/+1 counters on target creature you control.\n" +
        "III — Choose target creature you control. Until end of turn, double its power and " +
        "toughness and it gains trample."

    // I — The next red or green creature spell you cast this turn can be cast without paying its
    //     mana cost.
    sagaChapter(1) {
        effect = Effects.GrantNextSpellFreeCast(
            spellFilter = GameObjectFilter().withAnyColor(Color.RED, Color.GREEN) and GameObjectFilter.Creature
        )
    }

    // II — Put three +1/+1 counters on target creature you control.
    sagaChapter(2) {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, creature)
    }

    // III — Choose target creature you control. Until end of turn, double its power and toughness
    //       and it gains trample.
    sagaChapter(3) {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(
                power = DynamicAmount.EntityProperty(
                    EntityReference.Target(0),
                    EntityNumericProperty.Power
                ),
                toughness = DynamicAmount.EntityProperty(
                    EntityReference.Target(0),
                    EntityNumericProperty.Toughness
                ),
                target = creature
            ),
            Effects.GrantKeyword(Keyword.TRAMPLE, creature)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "197"
        artist = "Serena Malyon"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9032f05b-5c21-4996-90c1-268dc6dffbaa.jpg?1784182994"
    }
}
