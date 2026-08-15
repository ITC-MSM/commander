package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostZone
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Makeshift Mauler
 * {3}{U}
 * Creature — Zombie Horror
 * 4/5
 * As an additional cost to cast this spell, exile a creature card from your graveyard.
 */
val MakeshiftMauler = card("Makeshift Mauler") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Horror"
    oracleText = "As an additional cost to cast this spell, exile a creature card from your graveyard."
    power = 4
    toughness = 5

    additionalCost(
        Costs.additional.ExileCards(
            count = 1,
            filter = GameObjectFilter.Creature,
            fromZone = CostZone.GRAVEYARD
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "James Ryman"
        flavorText = "\"It always amazes me what perfectly good things people throw away.\"\n—Ludevic, necro-alchemist"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d869de57-9454-47ff-af14-eaefd387047a.jpg?1782714795"
    }
}
