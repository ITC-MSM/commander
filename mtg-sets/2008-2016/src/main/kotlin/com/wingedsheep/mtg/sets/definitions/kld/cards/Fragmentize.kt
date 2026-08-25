package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Fragmentize
 * {W}
 * Sorcery
 *
 * Destroy target artifact or enchantment with mana value 4 or less.
 *
 * The mana-value cap is part of the *target* requirement, not a resolution check — an illegal
 * target is illegal on announcement — so it rides on the [TargetFilter] as
 * `manaValueAtMost(4)` rather than as a condition around the destroy.
 */
val Fragmentize = card("Fragmentize") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or enchantment with mana value 4 or less."

    spell {
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter.ArtifactOrEnchantment.manaValueAtMost(4)),
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Jason Felix"
        flavorText = "\"Too much friction on your bearings, your countersinking is inconsistent, and there are lines in your casting. Dispose of it, and begin again.\"\n—Dovin Baan"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5bf09deb-2607-4eb0-94a7-9584e771fdfb.jpg?1783937234"
    }
}
