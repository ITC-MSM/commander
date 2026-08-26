package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Golden-Tail Disciple — Kamigawa: Neon Dynasty #15 (canonical printing)
 * {2}{W} · Enchantment Creature — Fox Monk · 2/3
 *
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 */
val GoldenTailDisciple = card("Golden-Tail Disciple") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Fox Monk"
    power = 2
    toughness = 3
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Jesper Ejsing"
        flavorText = "Students of the kitsune-led Golden-Tail Academy in Eiganjo are unparalleled " +
            "in both martial skill and dedication to the Imperial cause."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d631e7da-bd71-424a-a349-9bce0fd16b1f.jpg?1783923923"
    }
}
