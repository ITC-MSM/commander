package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Sunken Citadel
 * Land — Cave
 * This land enters tapped. As it enters, choose a color.
 * {T}: Add one mana of the chosen color.
 * {T}: Add two mana of the chosen color. Spend this mana only to activate abilities of land sources.
 *
 * The color is chosen as the land enters (replacement) and stored on the permanent; both mana
 * abilities read it via [Effects.AddManaOfChosenColor] (ManaColorSet.SourceChosenColor). The second
 * ability's mana carries a [ManaRestriction.CardTypeSpellsOrAbilitiesOnly] keyed to
 * [CardType.LAND] with `allowSpells = false, allowAbilities = true`, whose description renders as
 * "Spend this mana only to activate abilities of land sources".
 */
val SunkenCitadel = card("Sunken Citadel") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Cave"
    oracleText = "This land enters tapped. As it enters, choose a color.\n" +
        "{T}: Add one mana of the chosen color.\n" +
        "{T}: Add two mana of the chosen color. Spend this mana only to activate abilities of land sources."

    replacementEffect(EntersTapped())
    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChosenColor()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChosenColor(
            amount = 2,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.LAND,
                allowSpells = false,
                allowAbilities = true,
            ),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "285"
        artist = "Matteo Bassini"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e1c9b1a-e306-47bb-9f68-2083660319c0.jpg?1782694383"
    }
}
