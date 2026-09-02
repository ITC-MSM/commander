package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Selesnya Sentry
 * {2}{W}
 * Creature — Elephant Soldier
 * 3/2
 *
 * {5}{G}: Regenerate this creature.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * An off-colour regeneration ability — the activation cost's colour has nothing to do with the
 * card's own. There is no `Effects.Regenerate` facade; [RegenerateEffect] is the spelling.
 */
val SelesnyaSentry = card("Selesnya Sentry") {
    manaCost = "{2}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elephant Soldier"
    oracleText = "{5}{G}: Regenerate this creature."
    power = 3
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{5}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Wesley Burt"
        flavorText = "Ravnicans still tell tales about the Battle of Sumala where four Selesnya sentries held off an entire clan of Gruul warriors."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c34c1f5-d509-4c66-ba41-c7958ef5ee44.jpg?1783940374"
    }
}
