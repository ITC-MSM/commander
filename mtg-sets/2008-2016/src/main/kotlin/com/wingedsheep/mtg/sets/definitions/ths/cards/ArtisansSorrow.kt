package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Artisan's Sorrow
 * {3}{G}
 * Instant
 *
 * Destroy target artifact or enchantment. Scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 */
val ArtisansSorrow = card("Artisan's Sorrow") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment. Scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Composite(
            Effects.Move(t, Zone.GRAVEYARD, byDestruction = true),
            Effects.Scry(2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Jung Park"
        flavorText = "Some seers read bones or entrails. Others just like to break things."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4f38f7e-2369-48c9-9121-4b13c29ea869.jpg"
    }
}
