package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Leyline Prowler
 * {1}{B}{G}
 * Creature — Nightmare Beast
 * 2/3
 * Deathtouch, lifelink
 * {T}: Add one mana of any color.
 */
val LeylineProwler = card("Leyline Prowler") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Nightmare Beast"
    oracleText = "Deathtouch, lifelink\n" +
        "{T}: Add one mana of any color."
    power = 2
    toughness = 3

    keywords(Keyword.DEATHTOUCH, Keyword.LIFELINK)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "YW Tang"
        flavorText = "It feeds on the dark energies that course through the deep world—and on any other creature lured by the leyline's pull."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c56b4e8f-d48e-4bb0-883d-29f978033f65.jpg"
    }
}
