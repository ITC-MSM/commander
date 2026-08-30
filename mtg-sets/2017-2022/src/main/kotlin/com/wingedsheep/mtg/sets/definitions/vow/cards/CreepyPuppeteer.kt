package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Creepy Puppeteer (Innistrad: Crimson Vow #151)
 * {3}{R} · Creature — Human Rogue 4/3
 *
 * Haste
 * Whenever this creature attacks, if you attacked with exactly one other creature this combat, you
 * may have that creature's base power and toughness become 4/3 until end of turn.
 *
 * Implementation: the intervening "if" is an exact count of *other* creatures you control that
 * attacked this combat ([DynamicAmount.AggregateBattlefield] with `excludeSelf`, compared `EQ 1`
 * — the Stensia Uprising idiom, since the `AtLeast` helpers can't say "exactly"). Once that
 * holds, "that creature" *is* the one-member group "other creatures you control that attacked
 * this combat", so the set-base-P/T is fanned out with [Effects.ForEachInGroup] over the same
 * filter (a `GroupRef` on a stats effect is not expanded per-permanent — see Sanguine
 * Evangelist). `optional = true` puts the "you may" outermost, so there is a single prompt and
 * the intervening-if is re-checked on resolution (CR 603.4) before it is asked.
 * [Effects.SetBasePowerAndToughness] is Layer 7b, so the creature's own counters and pumps still
 * apply on top of the new 4/3 base.
 */
val CreepyPuppeteer = card("Creepy Puppeteer") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Rogue"
    power = 4
    toughness = 3
    oracleText = "Haste\n" +
        "Whenever this creature attacks, if you attacked with exactly one other creature this " +
        "combat, you may have that creature's base power and toughness become 4/3 until end of turn."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.Attacks
        interveningIf = Conditions.CompareAmounts(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature.attackedThisCombat(),
                excludeSelf = true,
            ),
            ComparisonOperator.EQ,
            DynamicAmount.Fixed(1),
        )
        optional = true
        effect = Effects.ForEachInGroup(
            GroupFilter(
                baseFilter = GameObjectFilter.Creature.youControl().attackedThisCombat(),
                excludeSelf = true,
            ),
            Effects.SetBasePowerAndToughness(4, 3, EffectTarget.Self, Duration.EndOfTurn),
        )
        description = "Whenever this creature attacks, if you attacked with exactly one other " +
            "creature this combat, you may have that creature's base power and toughness become " +
            "4/3 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Marie Magny"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0bd17b9f-fd93-47d4-9cc4-cd333d0004f5.jpg?1783924838"
    }
}
