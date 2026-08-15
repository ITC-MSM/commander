package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Yargle and Multani
 * {3}{B}{B}{G}
 * Legendary Creature — Frog Spirit Elemental
 * 18/6
 *
 * Vanilla — no rules text.
 */
val YargleAndMultani = card("Yargle and Multani") {
    manaCost = "{3}{B}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Frog Spirit Elemental"
    power = 18
    toughness = 6

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "256"
        artist = "Slawomir Maniak"
        flavorText = "\"I've heard much about you from my daughter,\" Multani rumbled. \"There was a time when I'd balk at your aid, phantom, but she has shown me the merit in Urborg's strange ways.\"\n\"Gnshhagghkkapphribbit,\" replied Yargle."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c15e244-14cc-46a5-abd4-66a58d1c0dd0.jpg?1783916936"
    }
}
