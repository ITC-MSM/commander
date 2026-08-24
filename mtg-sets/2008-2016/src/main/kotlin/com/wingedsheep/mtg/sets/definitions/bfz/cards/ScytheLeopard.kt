package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scythe Leopard
 * {G}
 * Creature — Cat
 * 1/1
 * Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val ScytheLeopard = card("Scythe Leopard") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 1
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Daniel Ljunggren"
        flavorText = "Eldrazi are not the leopard's preferred prey, but they are better than no prey at all."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89ef0054-a290-4c41-846d-12f8119c52ae.jpg?1783938185"
    }
}
