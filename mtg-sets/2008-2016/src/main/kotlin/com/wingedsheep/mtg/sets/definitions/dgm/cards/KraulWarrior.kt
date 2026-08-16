package com.wingedsheep.mtg.sets.definitions.dgm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kraul Warrior
 * {1}{G}
 * Creature — Insect Warrior
 * 2/2
 * {5}{G}: This creature gets +3/+3 until end of turn.
 */
val KraulWarrior = card("Kraul Warrior") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Warrior"
    power = 2
    toughness = 2
    oracleText = "{5}{G}: This creature gets +3/+3 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{5}{G}")
        effect = Effects.ModifyStats(3, 3, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "David Rapoza"
        flavorText = "The insectile kraul lurk in the tunnels below street level. Many are loyal to the Golgari Swarm, but others follow their own esoteric caste system."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f71da8cc-8773-4dcb-aca8-50a000142218.jpg?1783940036"
    }
}
