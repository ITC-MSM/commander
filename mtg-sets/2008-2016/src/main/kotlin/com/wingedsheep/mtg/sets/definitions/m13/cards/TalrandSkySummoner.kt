package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talrand, Sky Summoner
 * {2}{U}{U}
 * Legendary Creature — Merfolk Wizard
 * 2/2
 *
 * Whenever you cast an instant or sorcery spell, create a 2/2 blue Drake creature token with flying.
 */
val TalrandSkySummoner = card("Talrand, Sky Summoner") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Merfolk Wizard"
    oracleText = "Whenever you cast an instant or sorcery spell, create a 2/2 blue Drake creature token with flying."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Drake"),
            keywords = setOf(Keyword.FLYING),
        )
        description = "Whenever you cast an instant or sorcery spell, create a 2/2 blue Drake creature " +
            "token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "72"
        artist = "Svetlin Velinov"
        flavorText = "\"The seas are vast, but the skies are even more so. Why be content with one kingdom when I can rule them both?\""
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc1a6867-921d-4912-afae-c3c445ad81e7.jpg?1783940501"
    }
}
