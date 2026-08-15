package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blessed Light
 * {4}{W}
 * Instant
 * Exile target creature or enchantment.
 */
val BlessedLight = card("Blessed Light") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Exile target creature or enchantment."

    spell {
        val t = target("target", Targets.CreatureOrEnchantment)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Anthony Palumbo"
        flavorText = "\"Enchanted by mage-smiths and blessed by priests, Benalish windows let in light and cast out darkness.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4cf3eb65-0f52-49c1-8243-14ce05de9f3b.jpg?1562735305"
    }
}
