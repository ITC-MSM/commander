package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Broodhunter Wurm
 * {3}{G}
 * Creature — Wurm
 * 4/3
 *
 * Vanilla — no rules text.
 */
val BroodhunterWurm = card("Broodhunter Wurm") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Svetlin Velinov"
        flavorText = "The native creatures of Zendikar have adapted to live under rocks and crawl through underbrush in order to avoid the most voracious predators. The Eldrazi have yet to make such adjustments."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c11c852d-9c7c-4d9b-8e79-70ea5ac865df.jpg?1783938188"
    }
}
