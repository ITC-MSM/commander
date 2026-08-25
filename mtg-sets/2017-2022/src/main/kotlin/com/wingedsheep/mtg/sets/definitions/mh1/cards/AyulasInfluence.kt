package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Ayula's Influence
 * {G}{G}{G}
 * Enchantment
 * Discard a land card: Create a 2/2 green Bear creature token.
 *
 * The discard is the whole activation cost — a typed cost atom ([Costs.Discard] over
 * [GameObjectFilter.Land]), not an effect, and there is no mana component beside it. Same shape as
 * Trade Routes' second ability; see
 * [com.wingedsheep.mtg.sets.definitions.mmq.cards.TradeRoutes].
 */
val AyulasInfluence = card("Ayula's Influence") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Discard a land card: Create a 2/2 green Bear creature token."

    activatedAbility {
        cost = Costs.Discard(GameObjectFilter.Land)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Bear"),
        )
        description = "Discard a land card: Create a 2/2 green Bear creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "156"
        artist = "Kari Christensen"
        flavorText = "Ayula's runic clawmarks ensure her territories are never left defenseless."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b9296ca-39a8-4aad-be92-0a56c704e950.jpg?1783933100"
    }
}
