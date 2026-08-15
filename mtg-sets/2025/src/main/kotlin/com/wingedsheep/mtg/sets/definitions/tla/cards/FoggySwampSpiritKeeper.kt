package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Foggy Swamp Spirit Keeper
 * {1}{U}{B}
 * Creature — Human Druid Ally
 * 2/4
 *
 * Lifelink
 * Whenever you draw your second card each turn, create a 1/1 colorless Spirit creature
 * token with "This token can't block or be blocked by non-Spirit creatures."
 */
val FoggySwampSpiritKeeper = card("Foggy Swamp Spirit Keeper") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Human Druid Ally"
    power = 2
    toughness = 4
    oracleText = "Lifelink\n" +
        "Whenever you draw your second card each turn, create a 1/1 colorless Spirit " +
        "creature token with \"This token can't block or be blocked by non-Spirit creatures.\""

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(1),
            power = 1,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Spirit"),
            name = "Spirit",
            imageUri = "https://cards.scryfall.io/normal/front/f/5/f59eba51-458a-40e0-b754-999f91d5d839.jpg?1764117653",
            staticAbilities = listOf(
                CantBeBlockedExceptBy(
                    blockerFilter = GameObjectFilter.Creature.withSubtype("Spirit")
                ),
                CanOnlyBlockCreaturesWith(
                    blockerFilter = GameObjectFilter.Creature.withSubtype("Spirit")
                )
            )
        )
        description = "Whenever you draw your second card each turn, create a 1/1 colorless " +
            "Spirit creature token with \"This token can't block or be blocked by non-Spirit creatures.\""
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Airi Yoshihisa"
        flavorText = "\"Dang it, Slim, I told you: No eating our spirit buddies!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd624be9-e960-4485-a8a9-2d5f2dd51777.jpg?1764121614"
    }
}
