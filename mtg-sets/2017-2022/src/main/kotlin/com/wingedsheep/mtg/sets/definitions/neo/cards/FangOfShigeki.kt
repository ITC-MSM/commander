package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fang of Shigeki — Kamigawa: Neon Dynasty #183 (canonical printing)
 * {G} · Enchantment Creature — Snake Ninja · 1/1
 *
 * Deathtouch
 *
 * A vanilla-plus common. The Ninja creature type is printed flavour here — the card has no
 * ninjutsu ability, so nothing in the script reads it.
 */
val FangOfShigeki = card("Fang of Shigeki") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Snake Ninja"
    power = 1
    toughness = 1
    oracleText = "Deathtouch"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Yigit Koroglu"
        flavorText = "\"Let me give you a taste of the poison you've poured into our land.\"\n" +
            "—Shigeki, founder of the Order of Jukai"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2dd0fef1-209f-4de5-a736-8f9bca2faa0a.jpg?1783923851"
    }
}
