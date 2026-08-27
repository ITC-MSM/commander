package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Agent of Horizons
 * {2}{G}
 * Creature — Human Rogue
 * 3 / 2
 *
 * {2}{U}: This creature can't be blocked this turn.
 */
val AgentOfHorizons = card("Agent of Horizons") {
    manaCost = "{2}{G}"
    colorIdentity = "UG"
    typeLine = "Creature — Human Rogue"
    power = 3
    toughness = 2
    oracleText = "{2}{U}: This creature can't be blocked this turn."

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Clint Cearley"
        flavorText = "The light in the woods just before dawn reveals a glimmering network of branches, roots, and spiderwebs. The acolytes of Kruphix walk this lattice unseen."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc08576f-1d48-4db4-9bb9-e039f75c98b8.jpg"
    }
}
