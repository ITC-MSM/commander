package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Managorger Hydra
 * {2}{G}
 * Creature — Hydra
 * 1/1
 *
 * Trample
 * Whenever a player casts a spell, put a +1/+1 counter on this creature.
 */
val ManagorgerHydra = card("Managorger Hydra") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Hydra"
    oracleText = "Trample\nWhenever a player casts a spell, put a +1/+1 counter on this creature."
    power = 1
    toughness = 1

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.AnyPlayerCastsSpell
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "186"
        artist = "Lucas Graciano"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a08e5ab-1ff9-4470-ab28-fea19bb79845.jpg?1783938321"
    }
}
