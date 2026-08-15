package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grizzled Outrider
 * {4}{G}
 * Creature — Elf Warrior
 * 5/5
 *
 * Vanilla — no rules text.
 */
val GrizzledOutrider = card("Grizzled Outrider") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 5
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Cristi Balanescu"
        flavorText = "\"Sure, a horse might be faster. But I'd like to see a horse take the head clean off a troll in one swipe!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a1d4473-5317-4bdd-9cb9-93670acf52e9.jpg?1783928213"
    }
}
