package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wall of Pine Needles
 * {3}{G}
 * Creature — Plant Wall
 * 3/3
 *
 * Defender (This creature can't attack.)
 * {G}: Regenerate this creature.
 *
 * Nothing bespoke: defender is engine-live via `keywords(...)`, and the regeneration is the shared
 * [RegenerateEffect] on [EffectTarget.Self] behind a plain mana cost.
 */
val WallOfPineNeedles = card("Wall of Pine Needles") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    power = 3
    toughness = 3
    oracleText = "Defender (This creature can't attack.)\n" +
        "{G}: Regenerate this creature."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "274"
        artist = "Brian Snõddy"
        flavorText = "The power of the forest takes a hundred forms. Some are more surprising than others."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d879923-55fc-46ab-9306-5e1f10441c89.jpg"
    }
}
