package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Mold Demon
 * {5}{B}{B}
 * Creature — Fungus Demon
 * 6/6
 *
 * When this creature enters, sacrifice it unless you sacrifice two Swamps.
 */
val MoldDemon = card("Mold Demon") {
    manaCost = "{5}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Fungus Demon"
    power = 6
    toughness = 6
    oracleText = "When this creature enters, sacrifice it unless you sacrifice two Swamps."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(
            cost = Costs.pay.Sacrifice(GameObjectFilter.Land.withSubtype(Subtype.SWAMP), count = 2),
            suffer = SacrificeSelfEffect,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "112"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/649a33aa-7eac-4161-ae1a-fcbc758abccf.jpg?1783948064"
    }
}
