package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vessel of Paramnesia (Shadows over Innistrad #95)
 * {1}{U}
 * Enchantment
 *
 * {U}, Sacrifice this enchantment: Target player mills three cards. Draw a card.
 */
val VesselOfParamnesia = card("Vessel of Paramnesia") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "{U}, Sacrifice this enchantment: Target player mills three cards. Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Player)
        effect = Effects.Composite(
            Patterns.Library.mill(3, t),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Kieran Yanner"
        flavorText = "\"Write everything down. Trust me.\"\n—Vallon, Thraben inspector"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4596140-1113-4349-aabe-4e828ea574e8.jpg?1783937782"
    }
}
