package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Rattleclaw Mystic
 * {1}{G}
 * Creature — Human Shaman
 * 2/1
 * {T}: Add {G}, {U}, or {R}.
 * Morph {2} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)
 * When Rattleclaw Mystic is turned face up, add {G}{U}{R}.
 */
val RattleclawMystic = card("Rattleclaw Mystic") {
    manaCost = "{1}{G}"
    colorIdentity = "URG"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 1
    oracleText = "{T}: Add {G}, {U}, or {R}.\nMorph {2} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen this creature is turned face up, add {G}{U}{R}."

    // {T}: Add {G}
    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {T}: Add {U}
    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {T}: Add {R}
    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    morph = "{2}"

    // When Rattleclaw Mystic is turned face up, add {G}{U}{R}.
    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        effect = Effects.Composite(
            Effects.AddMana(Color.GREEN),
            Effects.AddMana(Color.BLUE),
            Effects.AddMana(Color.RED)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "144"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fb6c2e0-eeaa-4d60-aab7-2b8c739a9278.jpg?1562786444"
    }
}
