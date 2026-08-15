package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rakshasa Deathdealer
 * {B}{G}
 * Creature — Demon
 * 2/2
 * {B}{G}: This creature gets +2/+2 until end of turn.
 * {B}{G}: Regenerate this creature.
 */
val RakshasaDeathdealer = card("Rakshasa Deathdealer") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Demon"
    oracleText = "{B}{G}: This creature gets +2/+2 until end of turn.\n{B}{G}: Regenerate this creature."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{B}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{B}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "John Severin Brassell"
        flavorText = "\"Death fills me and makes me strong. You, it will reduce to nothing.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab4e0b33-dad1-4443-a7c7-a7bab067d04a.jpg?1699601221"
    }
}
