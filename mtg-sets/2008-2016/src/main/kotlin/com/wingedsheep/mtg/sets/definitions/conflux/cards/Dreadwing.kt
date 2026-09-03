package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dreadwing
 * {B}
 * Creature — Zombie
 * 1 / 1
 * {1}{U}{R}: This creature gets +3/+0 and gains flying until end of turn.
 *
 * The two printed halves are one [Effects.Composite] — the pump and the keyword grant both point at
 * [EffectTarget.Self] and both take the facade default `Duration.EndOfTurn`, so "until end of turn"
 * needs no argument. The off-colour activation cost is why the colour identity runs wider than the
 * `{B}` mana cost. Same shape as Hyalopterous Lemure.
 */
val Dreadwing = card("Dreadwing") {
    manaCost = "{B}"
    colorIdentity = "UBR"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 1
    oracleText = "{1}{U}{R}: This creature gets +3/+0 and gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{U}{R}")
        effect = Effects.Composite(
            Effects.ModifyStats(3, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Mark Hyzer"
        flavorText = "Dreadwings spring from lofty perches to surprise kathari in midflight. They smother their prey and then consume it as they glide gently toward the ground."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afb997b6-d872-438f-bf8a-db976bc27a2d.jpg"
    }
}
