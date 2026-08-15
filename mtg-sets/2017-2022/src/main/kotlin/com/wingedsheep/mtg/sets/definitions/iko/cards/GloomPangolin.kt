package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gloom Pangolin
 * {2}{B}
 * Creature — Nightmare Pangolin
 * 1/5
 *
 * Vanilla — no rules text.
 */
val GloomPangolin = card("Gloom Pangolin") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightmare Pangolin"
    power = 1
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "YW Tang"
        flavorText = "\"They say it survives on a purely bioluminescent diet. It's only active during the darkest of Indatha nights, making verification highly challenging.\"\n—Gannet, Skysail zoologist"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f135dd7-2a4f-4c83-9a90-76bcab3cc33d.jpg?1783931061"
    }
}
