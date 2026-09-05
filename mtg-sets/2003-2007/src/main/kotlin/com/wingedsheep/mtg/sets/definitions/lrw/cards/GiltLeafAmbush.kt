package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

// Oracle now uses Kindred in place of the originally printed Tribal card type.
val GiltLeafAmbush = card("Gilt-Leaf Ambush") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Kindred Instant — Elf"
    oracleText = "Create two 1/1 green Elf Warrior creature tokens. Clash with an opponent. If you win, those creatures gain deathtouch until end of turn. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value. Any amount of damage a creature with deathtouch deals to a creature is enough to destroy it.)"

    spell {
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/2/7/27b171ac-b2ef-4a80-92d1-6d9e71f3e3ca.jpg?1783942838"
        ).then(
            Patterns.Mechanic.clash(
                ForEachInCollectionEffect(
                    CREATED_TOKENS,
                    Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "214"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47bf7254-4302-41a0-bc87-d7dc437ff38d.jpg?1783942863"
        ruling("2024-06-07", "This cards was originally printed with the \"tribal\" card type. That card type has been replaced with \"kindred\". This change does not affect the gameplay function of this card.")
    }
}
