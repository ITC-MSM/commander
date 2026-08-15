package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Joo Dee, One of Many
 * {1}{B}
 * Creature — Human Advisor, 2/2
 * {B}, {T}: Surveil 1. Create a token that's a copy of this creature, then sacrifice an
 * artifact or creature. Activate only as a sorcery. (To surveil 1, look at the top card of
 * your library. You may put it into your graveyard.)
 */
val JooDeeOneOfMany = card("Joo Dee, One of Many") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Advisor"
    power = 2
    toughness = 2
    oracleText = "{B}, {T}: Surveil 1. Create a token that's a copy of this creature, then sacrifice an artifact or creature. Activate only as a sorcery. (To surveil 1, look at the top card of your library. You may put it into your graveyard.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        effect = Effects.Composite(
            Effects.Surveil(1),
            Effects.CreateTokenCopyOfTarget(target = EffectTarget.Self),
            Effects.Sacrifice(
                GameObjectFilter.Artifact or GameObjectFilter.Creature,
                count = 1,
                target = EffectTarget.Controller,
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Olena Richards"
        flavorText = "\"Hello! My name is Joo Dee. You're in Ba Sing Se now. Everyone is safe here.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7ae1439f-a0c4-42c2-a4f3-8851defa981e.jpg?1764120729"
    }
}
