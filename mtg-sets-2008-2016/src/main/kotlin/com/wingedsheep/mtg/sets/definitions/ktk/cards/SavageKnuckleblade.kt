package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Savage Knuckleblade
 * {G}{U}{R}
 * Creature — Ogre Warrior
 * 4/4
 * {2}{G}: Savage Knuckleblade gets +2/+2 until end of turn. Activate only once each turn.
 * {2}{U}: Return Savage Knuckleblade to its owner's hand.
 * {R}: Savage Knuckleblade gains haste until end of turn.
 */
val SavageKnuckleblade = card("Savage Knuckleblade") {
    manaCost = "{G}{U}{R}"
    colorIdentity = "URG"
    typeLine = "Creature — Ogre Warrior"
    oracleText = "{2}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn.\n{2}{U}: Return this creature to its owner's hand.\n{R}: This creature gains haste until end of turn."
    power = 4
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.ReturnToHand(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "197"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c9dddd3-7c8d-4669-8298-58149b142b8a.jpg?1562786291"
    }
}
