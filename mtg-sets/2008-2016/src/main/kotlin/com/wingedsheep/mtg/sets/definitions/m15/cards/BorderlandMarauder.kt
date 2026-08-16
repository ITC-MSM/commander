package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Borderland Marauder
 * {1}{R}
 * Creature — Human Warrior
 * 1/2
 * Whenever this creature attacks, it gets +2/+0 until end of turn.
 */
val BorderlandMarauder = card("Borderland Marauder") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 1
    toughness = 2
    oracleText = "Whenever this creature attacks, it gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Scott M. Fischer"
        flavorText = "Though she is rightly feared, there are relatively few tales of her deeds in battle, for few survive her raids."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12e29b2b-5fe3-4dee-b247-10cd139fe2d0.jpg?1783939177"
    }
}
