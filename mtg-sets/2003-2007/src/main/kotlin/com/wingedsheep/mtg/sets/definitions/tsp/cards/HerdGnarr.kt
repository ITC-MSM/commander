package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Herd Gnarr
 * {3}{G}
 * Creature — Beast
 * 2/2
 * Whenever another creature you control enters, this creature gets +2/+2 until end of turn.
 */
val HerdGnarr = card("Herd Gnarr") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "Whenever another creature you control enters, this creature gets +2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "Daren Bader"
        flavorText = "Long ago, the solitary gnarr was a sign of good luck. Now they have become wild pack hunters, a sign of impending danger."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cf4fd75-34b1-4afa-b8cd-777dfc9e6376.jpg"
    }
}
