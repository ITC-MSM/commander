package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Glimmerdust Nap
 * {2}{U}
 * Enchantment — Aura
 * Enchant tapped creature
 * Enchanted creature doesn't untap during its controller's untap step.
 *
 * The "tapped" restriction lives in the enchant clause only, so it is checked when the Aura is
 * cast and by the state-based attachment check — a creature that untaps later (through another
 * effect) keeps the Aura. `DOESNT_UNTAP` is the narrow untap-step flag rather than
 * `CANT_BECOME_UNTAPPED`: a Twiddle still untaps the napper.
 */
val GlimmerdustNap = card("Glimmerdust Nap") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant tapped creature\nEnchanted creature doesn't untap during its controller's untap step."

    auraTarget = Targets.TappedCreature

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Greg Staples"
        flavorText = "The dreams of giants are as long as time and as deep as the earth. Thus they are prized by the dream-harvesting fae."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e05a4ce4-dbd5-4c47-8b40-c145c7f5d06c.jpg?1783942901"
    }
}
