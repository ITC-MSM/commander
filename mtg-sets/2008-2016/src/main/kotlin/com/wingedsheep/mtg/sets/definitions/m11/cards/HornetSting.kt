package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hornet Sting
 * {G}
 * Instant
 *
 * Hornet Sting deals 1 damage to any target.
 *
 * The plain burn shape: one [Targets.Any] slot and [Effects.DealDamage] with no `damageSource`
 * override — the spell itself is the source, which is the default.
 */
val HornetSting = card("Hornet Sting") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Hornet Sting deals 1 damage to any target."

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "181"
        artist = "Matt Stewart"
        flavorText = "It was only then—to his infinite sorrow—that Gork realized hornets don't make honey."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af6b3bf7-bd09-4f0d-a670-2efc1c6d416f.jpg?1783941797"
    }
}
