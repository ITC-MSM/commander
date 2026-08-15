package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Meng Huo's Horde
 * {4}{G}
 * Creature — Human Soldier
 * 4/5
 *
 * Vanilla — no rules text.
 */
val MengHuosHorde = card("Meng Huo's Horde") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier"
    power = 4
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Li Tie"
        flavorText = "Through his allies, Meng Huo commanded several hundred thousand troops composed of cavalry, rattan-armored warriors, infantry, naked men with swords, and wild animals."
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1642802-055e-44d2-a261-c2a45ad515e8.jpg?1783946099"
    }
}
