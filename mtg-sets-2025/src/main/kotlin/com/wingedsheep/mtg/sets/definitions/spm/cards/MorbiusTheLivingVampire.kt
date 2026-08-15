package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Morbius the Living Vampire
 * {2}{U}{B}
 * Legendary Creature — Vampire Scientist Villain
 * 3/1
 * Flying, vigilance, lifelink
 * {U}{B}, Exile this card from your graveyard: Look at the top three cards of your library. Put one of them into your hand and the rest on the bottom of your library in any order.
 */
val MorbiusTheLivingVampire = card("Morbius the Living Vampire") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Vampire Scientist Villain"
    oracleText = "Flying, vigilance, lifelink\n{U}{B}, Exile this card from your graveyard: Look at the top three cards of your library. Put one of them into your hand and the rest on the bottom of your library in any order."
    power = 3
    toughness = 1
    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.LIFELINK)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{B}"), Costs.ExileSelf)
        effect = Patterns.Library.lookAtTopAndKeep(
            count = DynamicAmount.Fixed(3),
            keepCount = DynamicAmount.Fixed(1),
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.ControllerChooses
        )
        activateFromZone = Zone.GRAVEYARD
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Borja Pindado"
        flavorText = "\"Nothing matters—except the ravening thirst of Morbius! A thirst which must be quenched!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b978d0e2-f5a7-4ade-befc-11b406e84477.jpg?1757377753"
    }
}
