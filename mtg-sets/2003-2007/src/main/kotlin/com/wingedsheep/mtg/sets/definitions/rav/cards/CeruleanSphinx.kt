package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cerulean Sphinx
 * {4}{U}{U}
 * Creature — Sphinx
 * 5/5
 * Flying
 * {U}: This creature's owner shuffles it into their library.
 *
 * Alabaster Dragon's self-shuffle, on an activated ability instead of a dies trigger:
 * [Effects.ShuffleIntoLibrary] over [EffectTarget.Self] is `MoveToZoneEffect(… LIBRARY, Shuffled)`,
 * and a card moved to a library always lands in its *owner's* one — so the printed "its owner"
 * needs no extra wiring, and a stolen Sphinx still shuffles back to the player who owns it.
 */
val CeruleanSphinx = card("Cerulean Sphinx") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    oracleText = "Flying\n{U}: This creature's owner shuffles it into their library."
    power = 5
    toughness = 5
    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.ShuffleIntoLibrary(EffectTarget.Self)
        description = "This creature's owner shuffles it into their library."
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Jim Murray"
        flavorText = "\"About the sphinx, I have mixed feelings. Their wisdom I crave, but their " +
            "secrecy I can't tolerate.\"\n—Szadek"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dc95d4a6-ad4b-46c5-8a75-e70102363844.jpg"
    }
}
