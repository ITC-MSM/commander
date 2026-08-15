package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pit Imp
 * {B}
 * Creature — Imp
 * 0/1
 * Flying
 * {B}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn.
 */
val PitImp = card("Pit Imp") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    power = 0
    toughness = 1
    oracleText = "Flying\n{B}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.MaxPerTurn(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Phil Foglio"
        flavorText = "The moans of the Death Pits were underscored by the chattering of imps, for whom the ship was cause for much discussion."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24c7acfe-b5b2-426f-a5a1-1ff8ef7ebf72.jpg?1562052816"
    }
}
