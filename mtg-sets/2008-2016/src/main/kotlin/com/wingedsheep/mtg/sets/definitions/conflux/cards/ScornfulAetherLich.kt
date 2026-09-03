package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scornful Aether-Lich
 * {3}{U}
 * Artifact Creature — Zombie Wizard
 * 2/4
 * {W}{B}: This creature gains fear and vigilance until end of turn. (Attacking doesn't cause it to
 * tap, and it can't be blocked except by artifact creatures and/or black creatures.)
 *
 * One activation granting two keywords: [Effects.Composite] over two [Effects.GrantKeyword] calls
 * onto [EffectTarget.Self], each defaulting to `Duration.EndOfTurn`. Two grants rather than one
 * because a grant carries a single keyword — the same shape Gravelgill Duo uses for its fear.
 */
val ScornfulAetherLich = card("Scornful Aether-Lich") {
    manaCost = "{3}{U}"
    colorIdentity = "BUW"
    typeLine = "Artifact Creature — Zombie Wizard"
    power = 2
    toughness = 4
    oracleText = "{W}{B}: This creature gains fear and vigilance until end of turn. " +
        "(Attacking doesn't cause it to tap, and it can't be blocked except by artifact creatures and/or black creatures.)"

    activatedAbility {
        cost = Costs.Mana("{W}{B}")
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.FEAR, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Steven Belledin"
        flavorText = "\"With no flesh, there is no pain, no hesitation, no emotion of any kind. He is crafted perfection.\" —Tezzeret"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75cdb2ff-6e30-4766-9166-9036a0bdb809.jpg"
    }
}
