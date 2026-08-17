package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Arasta of the Endless Web
 * {2}{G}{G}
 * Legendary Enchantment Creature — Spider
 * 3/5
 *
 * Reach
 * Whenever an opponent casts an instant or sorcery spell, create a 1/2 green Spider creature
 * token with reach.
 *
 * The trigger watches *opponents only* ([Triggers.opponentCasts]), so Arasta's controller casting
 * their own removal spell doesn't feed them Spiders.
 */
val ArastaOfTheEndlessWeb = card("Arasta of the Endless Web") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Enchantment Creature — Spider"
    power = 3
    toughness = 5
    oracleText = "Reach\n" +
        "Whenever an opponent casts an instant or sorcery spell, create a 1/2 green Spider " +
        "creature token with reach."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.InstantOrSorcery)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Spider"),
            keywords = setOf(Keyword.REACH)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "165"
        artist = "Sam Rowan"
        flavorText = "Her webs, spun from her own hair, reach from Nyx to the mortal world and even into the Underworld."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c38833a-96c5-48b5-8dd8-23f10e798537.jpg"
    }
}
