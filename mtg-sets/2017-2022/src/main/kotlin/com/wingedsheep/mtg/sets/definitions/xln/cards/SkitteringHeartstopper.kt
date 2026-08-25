package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skittering Heartstopper
 * {B}
 * Creature — Insect
 * 1/2
 *
 * {B}: This creature gains deathtouch until end of turn.
 */
val SkitteringHeartstopper = card("Skittering Heartstopper") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    oracleText = "{B}: This creature gains deathtouch until end of turn."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Aaron Miller"
        flavorText = "It flows like water over the forest floor, as deadly as the swiftest current."
        imageUri = "https://cards.scryfall.io/normal/front/0/7/0722fbdf-c092-4e80-913f-29390177cdcb.jpg"
    }
}
