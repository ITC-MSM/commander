package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Glimmerbell
 * {1}{U}
 * Creature — Elemental Jellyfish
 * 1/3
 * Flying
 * {1}{U}: Untap this creature.
 *
 * A pseudo-vigilance jellyfish: the untap is the plain [Effects.Untap] on [EffectTarget.Self],
 * bought with a repeatable mana-only [Costs.Mana] activation at instant speed.
 */
val Glimmerbell = card("Glimmerbell") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Jellyfish"
    power = 1
    toughness = 3
    oracleText = "Flying\n{1}{U}: Untap this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Simon Dominic"
        flavorText = "\"Their paths aren't random, but they don't float with the wind. Could they be following surges in crystalline energy?\"\n—Naireh, Ketria elementalist"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54f71f8a-546a-465e-a254-09a3ae873ef4.jpg"
    }
}
