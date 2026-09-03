package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thunder Drake
 * {3}{U}
 * Creature — Elemental Drake
 * 2/3
 * Flying
 * Whenever you cast your second spell each turn, put a +1/+1 counter on this creature.
 */
val ThunderDrake = card("Thunder Drake") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Drake"
    oracleText = "Flying\n" +
        "Whenever you cast your second spell each turn, put a +1/+1 counter on this creature."
    power = 2
    toughness = 3
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Yeong-Hao Han"
        flavorText = "The arrival of the Planar Bridge caused eddies in the aether, creating some entirely new species while transforming others."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f88a9bc4-1b15-4a91-9376-730d2f5b3336.jpg"
    }
}
