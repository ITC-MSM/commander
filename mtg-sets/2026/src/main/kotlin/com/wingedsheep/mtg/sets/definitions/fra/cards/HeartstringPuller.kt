package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val HeartstringPuller = card("Heartstring Puller") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elf Sorcerer"
    power = 3
    toughness = 1
    oracleText = "Trample\nWhen this creature enters, create a 2/2 colorless Wizard Soldier creature token named Cadet."

    keywords(Keyword.TRAMPLE)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(power = 2, toughness = 2, name = "Cadet", creatureTypes = setOf("Wizard", "Soldier"), imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318")
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Jessica Liu"
        flavorText = "\"Come on cadet, I'll show you the ropes!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbfe3354-7ced-4773-9a4e-a937ae9f94f8.jpg?1789556808"
        inBooster = false
    }
}
