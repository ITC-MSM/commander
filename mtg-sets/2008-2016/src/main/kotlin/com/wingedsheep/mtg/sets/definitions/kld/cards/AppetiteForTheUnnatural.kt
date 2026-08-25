package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Appetite for the Unnatural
 * {2}{G}
 * Instant
 * Destroy target artifact or enchantment. You gain 2 life.
 *
 * Solemn Offering's shape at instant speed: one [Targets.ArtifactOrEnchantment] slot, then
 * [Effects.Destroy] chained to the untargeted life gain — the gain happens whether or not the
 * permanent is actually destroyed, so it is a sequenced second effect rather than a rider.
 */
val AppetiteForTheUnnatural = card("Appetite for the Unnatural") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment. You gain 2 life."

    spell {
        val t = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
            .then(Effects.GainLife(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Zoltan Boros"
        flavorText = "\"I'd just let the monkey have it if I were you. You can make another, but the same can't be said of your fingers.\"\n—Turni, Greenwheel groundskeeper"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8aa8840-31f8-4263-b992-40584e31595a.jpg?1783937185"
    }
}
