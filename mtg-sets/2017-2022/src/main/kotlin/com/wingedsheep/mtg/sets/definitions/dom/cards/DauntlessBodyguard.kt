package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dauntless Bodyguard
 * {W}
 * Creature — Human Knight
 * 2/1
 * As Dauntless Bodyguard enters the battlefield, choose another creature you control.
 * Sacrifice Dauntless Bodyguard: The chosen creature gains indestructible until end of turn.
 */
val DauntlessBodyguard = card("Dauntless Bodyguard") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 1
    oracleText = "As Dauntless Bodyguard enters the battlefield, choose another creature you control.\nSacrifice Dauntless Bodyguard: The chosen creature gains indestructible until end of turn."

    replacementEffect(EntersWithChoice(ChoiceType.CREATURE_ON_BATTLEFIELD))

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.ChosenCreature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Manuel Castañón"
        flavorText = "The Benalish aristocracy is hereditary, but the loyalty of its subjects is earned."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3f82012-5c33-47bc-a3c8-56ae2eea1fb9.jpg?1562740636"
    }
}
