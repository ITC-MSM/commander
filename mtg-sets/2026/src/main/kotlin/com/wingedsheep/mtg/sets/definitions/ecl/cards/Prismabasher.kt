package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Prismabasher
 * {4}{G}{G}
 * Creature — Elemental
 * 6/6
 *
 * Trample
 * Vivid — When this creature enters, up to X target creatures you control get +X/+X
 * until end of turn, where X is the number of colors among permanents you control.
 *
 * X is locked twice (per the Scryfall ruling): once when the trigger goes on the
 * stack — capping the number of selectable targets — and again at resolution to set
 * the +N/+N bonus. The same `colorsAmongPermanents()` value powers both the
 * `dynamicMaxCount` (engine resolves the cap at trigger placement; UI shows
 * "selected / X") and the `ModifyStats` amount.
 */
val Prismabasher = card("Prismabasher") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    oracleText = "Trample\n" +
        "Vivid — When this creature enters, up to X target creatures you control get +X/+X " +
        "until end of turn, where X is the number of colors among permanents you control."
    power = 6
    toughness = 6

    keywords(Keyword.TRAMPLE, Keyword.VIVID)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to X target creatures you control",
            TargetCreature(
                optional = true,
                filter = TargetFilter.CreatureYouControl,
                dynamicMaxCount = DynamicAmounts.colorsAmongPermanents()
            )
        )
        effect = ForEachTargetEffect(
            listOf(
                Effects.ModifyStats(
                    power = DynamicAmounts.colorsAmongPermanents(),
                    toughness = DynamicAmounts.colorsAmongPermanents(),
                    target = EffectTarget.ContextTarget(0)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Aaron Miller"
        flavorText = "Driven by an unknown hunger, it charges from aurora to aurora leaving trails of wild magic in its wake."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65c057a7-70af-4464-bd8d-1e7e158d1ae7.jpg?1767872056"
        ruling(
            "2025-11-17",
            "The value of X is calculated when Prismabasher's last ability triggers for the purpose " +
                "of defining the maximum number of target creatures. It is calculated again when the " +
                "ability resolves to determine the amount of the power and toughness bonus."
        )
    }
}
