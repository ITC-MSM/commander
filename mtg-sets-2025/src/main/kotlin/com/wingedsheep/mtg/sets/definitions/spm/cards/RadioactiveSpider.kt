package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Radioactive Spider
 * {G}
 * Creature — Spider
 * 1/1
 * Reach, deathtouch
 * Fateful Bite — {2}, Sacrifice this creature: Search your library for a Spider Hero card, reveal it, put it into your hand, then shuffle. Activate only as a sorcery.
 */
val RadioactiveSpider = card("Radioactive Spider") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    oracleText = "Reach, deathtouch\nFateful Bite — {2}, Sacrifice this creature: Search your library for a Spider Hero card, reveal it, put it into your hand, then shuffle. Activate only as a sorcery."
    power = 1
    toughness = 1
    keywords(Keyword.REACH, Keyword.DEATHTOUCH)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype("Spider").withSubtype("Hero"),
            destination = SearchDestination.HAND,
            reveal = true
        )
        timing = TimingRule.SorcerySpeed
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "111"
        artist = "Pavel Kolomeyets"
        flavorText = "\"Ow! A spider! It bit me! But why is it glowing that way?\"\n—Peter Parker"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2d267f5-7f12-45f8-8fcb-e0ba3fbdeddc.jpg?1757377503"
    }
}
