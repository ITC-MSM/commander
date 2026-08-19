package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kinsbaile Skirmisher
 * {1}{W}
 * Creature — Kithkin Soldier
 * 2/2
 * When this creature enters, target creature gets +1/+1 until end of turn.
 */
val KinsbaileSkirmisher = card("Kinsbaile Skirmisher") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, target creature gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, creature)
        description = "target creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Thomas Denmark"
        flavorText = "\"If a boggart even dares breathe near one of my kin, I'll know. And I'll not be happy.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/6145bcd5-a583-4a04-9a13-a64ecdf4425a.jpg?1783942913"
    }
}
