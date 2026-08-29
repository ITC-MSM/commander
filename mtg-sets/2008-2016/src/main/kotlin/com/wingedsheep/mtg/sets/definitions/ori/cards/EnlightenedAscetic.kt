package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Enlightened Ascetic
 * {1}{W}
 * Creature — Cat Monk
 * 1/1
 *
 * When this creature enters, you may destroy target enchantment.
 */
val EnlightenedAscetic = card("Enlightened Ascetic") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Monk"
    oracleText = "When this creature enters, you may destroy target enchantment."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target("target enchantment", Targets.Enchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "James Zapata"
        flavorText = "\"I do not reject the gods. I reject their authority, their pettiness, and their arrogance.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76549fc3-5798-4c70-bb70-802b6f597eb7.jpg"
    }
}
