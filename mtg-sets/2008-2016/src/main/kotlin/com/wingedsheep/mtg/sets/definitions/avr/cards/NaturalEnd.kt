package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Natural End
 * {2}{G}
 * Instant
 *
 * Destroy target artifact or enchantment. You gain 3 life.
 *
 * Cursebreak's green sibling — one flat [Effects.Composite] of destroy-then-gain, with the target
 * widened to [Targets.ArtifactOrEnchantment] (a single `Or` card predicate, not two).
 */
val NaturalEnd = card("Natural End") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment. You gain 3 life."

    spell {
        val permanent = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Composite(
            Effects.Destroy(permanent),
            Effects.GainLife(3),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Scott Chou"
        flavorText = "The haunted blade shattered, and the geist drifted gratefully to the Blessed Sleep."
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95d25235-de1c-4b67-9712-24f0564bd2bf.jpg?1783940664"
    }
}
