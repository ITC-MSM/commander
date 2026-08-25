package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Essence Extraction
 * {1}{B}{B}
 * Instant
 *
 * Essence Extraction deals 3 damage to target creature and you gain 3 life.
 *
 * A plain composite: the damage is attributed to the spell itself, so no explicit damage source is
 * written, and the life gain names no player, which is the SDK's default of the effect's
 * controller. The two halves are independent — the life is gained even if the creature has already
 * left the battlefield.
 */
val EssenceExtraction = card("Essence Extraction") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Essence Extraction deals 3 damage to target creature and you gain 3 life."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(3, t)
            .then(Effects.GainLife(3))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Min Yum"
        flavorText = "The \"gifted\" among the aetherborn can draw the life essence of other living beings into themselves."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7693c10-5ebb-4896-bb60-63d03577dd60.jpg?1783937209"
    }
}
