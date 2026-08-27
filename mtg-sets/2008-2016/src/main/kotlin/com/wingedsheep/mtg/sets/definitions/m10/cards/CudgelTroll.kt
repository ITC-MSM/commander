package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cudgel Troll
 * {2}{G}{G}
 * Creature — Troll
 * 4/3
 *
 * {G}: Regenerate this creature. (The next time this creature would be destroyed this turn, instead
 * tap it, remove it from combat, and heal all damage on it.)
 *
 * - "Regenerate this creature" is [RegenerateEffect] on [EffectTarget.Self] — a replacement shield
 *   over the next destruction this turn, not damage prevention. There is no `Effects.Regenerate`
 *   facade; the effect class is the shipped spelling (Cinderbones, Marrow Bats, Kin-Tree Warden).
 * - The reminder text is printed on the card and kept verbatim in the oracle text.
 */
val CudgelTroll = card("Cudgel Troll") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Troll"
    power = 4
    toughness = 3
    oracleText = "{G}: Regenerate this creature. (The next time this creature would be destroyed " +
        "this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{G}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d779b14c-a100-4382-9e7c-0969efda73ec.jpg?1783942365"
    }
}
