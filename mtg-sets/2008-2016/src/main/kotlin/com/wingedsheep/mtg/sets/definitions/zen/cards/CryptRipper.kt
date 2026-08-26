package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Crypt Ripper
 * {2}{B}{B}
 * Creature — Shade
 * 2/2
 * Haste
 * {B}: This creature gets +1/+1 until end of turn.
 *
 * The classic Shade pump: an unrestricted firebreathing-style ability on itself, so the bonus
 * stacks once per activation and wears off at end of turn.
 */
val CryptRipper = card("Crypt Ripper") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    power = 2
    toughness = 2
    oracleText = "Haste\n" +
        "{B}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Dave Kendall"
        flavorText = "The tender light of the living quickens the pulse of the dead."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/9920e91d-58c1-4c1a-a177-43423db96842.jpg"
    }
}
