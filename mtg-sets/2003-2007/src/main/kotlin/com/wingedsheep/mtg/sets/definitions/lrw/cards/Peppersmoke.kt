package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Peppersmoke
 * {B}
 * Kindred Instant — Faerie
 * Target creature gets -1/-1 until end of turn. If you control a Faerie, draw a card.
 */
val Peppersmoke = card("Peppersmoke") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Kindred Instant — Faerie"
    oracleText = "Target creature gets -1/-1 until end of turn. If you control a Faerie, draw a card."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(-1, -1, creature),
            ConditionalEffect(
                condition = Conditions.ControlPermanentOfType(Subtype.FAERIE),
                effect = Effects.DrawCards(1)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Rebecca Guay"
        flavorText = "Like being trapped in a perpetual sneeze, faerie-dust poisoning is both exhilarating and agonizing."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/568586ca-8a02-4a77-bd65-c0b4a74c429d.jpg?1783942884"
    }
}
