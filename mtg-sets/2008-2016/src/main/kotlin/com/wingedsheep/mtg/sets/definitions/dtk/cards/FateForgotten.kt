package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fate Forgotten
 * {2}{W}
 * Instant
 *
 * Exile target artifact or enchantment.
 *
 * "Artifact or enchantment" is one target with an alternation inside its filter, not two targets —
 * [Targets.ArtifactOrEnchantment] is that filter. Exile is a plain zone move, so no destruction or
 * regeneration hooks apply.
 */
val FateForgotten = card("Fate Forgotten") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Exile target artifact or enchantment."

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Cliff Childs"
        flavorText = "When Sarkhan saved Ugin in Tarkir's past, it changed Tarkir's future. The Sultai no longer exist, having been supplanted by the dragonlord Silumgar and his clan."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b2161d9-ad52-45a6-9be9-e7ff09ec8f5a.jpg?1783938617"
    }
}
