package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fade into Antiquity
 * {2}{G}
 * Sorcery
 *
 * Exile target artifact or enchantment.
 */
val FadeIntoAntiquity = card("Fade into Antiquity") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Exile target artifact or enchantment."

    spell {
        val permanent = target("artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Noah Bradley"
        flavorText = "\"Are the gods angry at our discontent with what they give us, or jealous that we made a thing they cannot?\"\n—Kleon the Iron-Booted"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e43e46a6-f7de-482a-a386-73932d1d9002.jpg"
    }
}
