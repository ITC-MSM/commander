package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity


/**
 * Undercity Dire Rat
 * {1}{B}
 * Creature — Rat
 * 2/2
 * Rat Tail — When this creature dies, create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 */
val UndercityDireRat = card("Undercity Dire Rat") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat"
    oracleText = "Rat Tail — When this creature dies, create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 2
    toughness = 2
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateTreasure(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Leonardo Santanna"
        flavorText = "\"I finally worked out how to get the door on the left side open! I hope there's somethin' in there besides rats...\"\n—Kytes, street urchin"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/274788f4-fbf3-4a15-bdc0-f513a2fde30d.jpg"
    }
}
