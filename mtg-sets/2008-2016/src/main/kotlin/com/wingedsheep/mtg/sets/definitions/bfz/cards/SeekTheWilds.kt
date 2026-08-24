package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Seek the Wilds
 * {1}{G}
 * Sorcery
 * Look at the top four cards of your library. You may reveal a creature or land card from among them and put it into your hand. Put the rest on the bottom of your library in any order.
 *
 * "…in any order" is [CardOrder.ControllerChooses], not the recipe's default random order.
 */
val SeekTheWilds = card("Seek the Wilds") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Look at the top four cards of your library. You may reveal a creature or land card from among " +
        "them and put it into your hand. Put the rest on the bottom of your library in any order."

    spell {
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.CreatureOrLand,
            prompt = "You may reveal a creature or land card from among them and put it into your hand",
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.ControllerChooses,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Anna Steinbauer"
        flavorText = "\"In my downtime I used to paint the vistas. Now the vistas disappear faster than my " +
            "downtime.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/2/824f44fe-ee16-4b15-a308-5c620cfd3d93.jpg?1783938185"
    }
}
