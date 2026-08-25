package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddDynamicCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Black Sun's Zenith — Mirrodin Besieged #39 (canonical / earliest real printing, 2011)
 * {X}{B}{B} · Sorcery
 *
 * Put X -1/-1 counters on each creature. Shuffle Black Sun's Zenith into its owner's library.
 *
 * Darkness Descends' shape (`ForEachInGroup` over `GroupFilter.AllCreatures`, `EffectTarget.Self`
 * rebound to each creature in turn) with the fixed count swapped for the dynamic one. Counters
 * rather than a -X/-X effect is the whole point of the card: they stick past end of turn, and a
 * creature reduced to 0 toughness dies to state-based actions.
 */
val BlackSunsZenith = card("Black Sun's Zenith") {
    manaCost = "{X}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Put X -1/-1 counters on each creature. Shuffle Black Sun's Zenith into its " +
        "owner's library."

    spell {
        effect = Effects.ForEachInGroup(
            filter = GroupFilter.AllCreatures,
            effect = AddDynamicCountersEffect(
                counterType = Counters.MINUS_ONE_MINUS_ONE,
                amount = DynamicAmount.XValue,
                target = EffectTarget.Self,
            ),
        )
        selfShuffleIntoLibrary()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Daniel Ljunggren"
        flavorText = "\"Under the suns, Mirrodin kneels and begs us for perfection.\"\n" +
            "—Geth, Lord of the Vault"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03bdcf52-50b8-42c0-9665-931d83f5f314.jpg?1783941385"
        ruling(
            "2011-06-01",
            "If this spell doesn't resolve, none of its effects occur. In particular, it will go " +
                "to the graveyard rather than to its owner's library."
        )
    }
}
