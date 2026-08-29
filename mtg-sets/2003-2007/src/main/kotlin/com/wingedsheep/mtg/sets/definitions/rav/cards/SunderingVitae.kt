package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sundering Vitae
 * {2}{G}
 * Instant
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Destroy target artifact or enchantment.
 */
val SunderingVitae = card("Sundering Vitae") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Destroy target artifact or enchantment."

    keywords(Keyword.CONVOKE)

    spell {
        val t = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Shishizaru"
        flavorText = "Centuries of wind, rain, and roots compressed into an instant of destruction: such is the power of Selesnya."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1ab15b40-c5c8-4d77-a4c0-982b6bf94267.jpg"
    }
}
