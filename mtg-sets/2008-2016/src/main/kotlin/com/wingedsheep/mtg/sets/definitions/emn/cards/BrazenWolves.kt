package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brazen Wolves
 * {2}{R}
 * Creature — Wolf
 * 2/3
 * Whenever this creature attacks, it gets +2/+0 until end of turn.
 */
val BrazenWolves = card("Brazen Wolves") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 3
    oracleText = "Whenever this creature attacks, it gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Nils Hamm"
        flavorText = "With fewer patrols about, Kessig's roads have become prime hunting grounds."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab8e2ece-3c66-4f34-9042-fc02639c6a79.jpg?1783937466"
    }
}
