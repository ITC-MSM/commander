package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mirror Entity
 * {2}{W}
 * Creature — Shapeshifter
 * 1/1
 *
 * Changeling (This card is every creature type.)
 * {X}: Until end of turn, creatures you control have base power and toughness X/X and gain all
 * creature types.
 *
 * The group is gathered when the ability resolves, so creatures that arrive later in the turn
 * are unaffected — the ability doesn't keep applying to whatever you control at any moment.
 * Mirror Entity is in that group and sets its own base P/T too.
 *
 * "Base power and toughness X/X" is layer 7b (setting), so it *overwrites* earlier setting
 * effects but sits underneath +N/+N counters and pump spells, which apply in 7c/7d. Activating
 * for X=1 after a big X therefore shrinks your team; activating twice, the later activation
 * wins on timestamp. `SetBasePowerAndToughness` freezes [DynamicAmount.XValue] at resolution
 * rather than re-reading it, which is what a one-shot activation needs.
 *
 * "Gain all creature types" is Changeling granted for the turn (CR 702.73) — the same expansion
 * the printed keyword uses, so the pumped team is Elves and Goblins and Merfolk at once for
 * every tribal lord you control.
 */
val MirrorEntity = card("Mirror Entity") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)\n" +
        "{X}: Until end of turn, creatures you control have base power and toughness X/X and " +
        "gain all creature types."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Mana("{X}")
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.Composite(
                Effects.SetBasePowerAndToughness(
                    power = DynamicAmount.XValue,
                    toughness = DynamicAmount.XValue,
                    target = EffectTarget.Self,
                    duration = Duration.EndOfTurn
                ),
                Effects.GrantKeyword(Keyword.CHANGELING, EffectTarget.Self, Duration.EndOfTurn)
            )
        )
        description = "Until end of turn, creatures you control have base power and toughness " +
            "X/X and gain all creature types."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "31"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Unaware of Lorwyn's diversity, it sees only itself, reflected a thousand " +
            "times over."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/adfff880-cbf6-4085-bc05-c72658b75f25.jpg?1783942911"

        ruling("2021-03-19", "Changeling applies in all zones, not just the battlefield.")
        ruling(
            "2021-03-19",
            "Mirror Entity's ability overwrites any effects that previously set a creature's " +
                "base power and/or toughness. Any existing effects or counters that raise or " +
                "lower a creature's power and/or toughness continue to apply to the creature's " +
                "newly-set power and toughness."
        )
        ruling(
            "2021-03-19",
            "Activating the ability with X = 0 will cause all creatures you control to become " +
                "0/0 and be put into the graveyard."
        )
    }
}
