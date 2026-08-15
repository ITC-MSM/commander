package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.MustAttack

/**
 * Dauthi Slayer
 * {B}{B}
 * Creature — Dauthi Soldier
 * 2/2
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * This creature attacks each combat if able.
 */
val DauthiSlayer = card("Dauthi Slayer") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Dauthi Soldier"
    power = 2
    toughness = 2
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "This creature attacks each combat if able."

    keywords(Keyword.SHADOW)

    staticAbility {
        ability = MustAttack()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Dermot Power"
        flavorText = "\"They have knives for every soul.\"\n—Lyna, Soltari emissary"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/652ccd79-aefd-4b45-b747-75190da0cfc6.jpg?1562054258"
    }
}
