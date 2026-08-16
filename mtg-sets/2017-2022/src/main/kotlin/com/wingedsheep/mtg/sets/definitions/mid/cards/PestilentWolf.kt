package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pestilent Wolf
 * {1}{G}
 * Creature — Wolf
 * 2/2
 * {2}{G}: This creature gains deathtouch until end of turn.
 */
val PestilentWolf = card("Pestilent Wolf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 2
    oracleText = "{2}{G}: This creature gains deathtouch until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Oriana Menendez"
        flavorText = "Wolves that feast on zombie flesh and survive carry the foul diseases of the dead wherever they roam."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2686431-8d9c-4b9c-998f-38b5ae113d4a.jpg?1783925575"
    }
}
