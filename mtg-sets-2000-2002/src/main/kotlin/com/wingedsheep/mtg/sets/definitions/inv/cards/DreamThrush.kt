package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.OptionType

/**
 * Dream Thrush
 * {1}{U}
 * Creature — Bird
 * 1/1
 * Flying
 * {T}: Target land becomes the basic land type of your choice until end of turn.
 *
 * "Becomes" replaces the land's existing land subtypes (Rule 305.7), so the land loses
 * the mana abilities of its old types and gains those of the chosen type — modeled with
 * [Effects.SetLandType] rather than the additive [Effects.AddSubtype].
 */
val DreamThrush = card("Dream Thrush") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "{T}: Target land becomes the basic land type of your choice until end of turn."

    keywords(Keyword.FLYING)

    val chosenKey = "chosenLandType"

    activatedAbility {
        val land = target("target land", Targets.Land)
        cost = AbilityCost.Tap
        effect = Effects.Composite(
            Effects.ChooseOption(
                optionType = OptionType.BASIC_LAND_TYPE,
                storeAs = chosenKey
            ),
            Effects.SetLandType(
                target = land,
                duration = Duration.EndOfTurn,
                fromChosenValueKey = chosenKey
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "D. J. Cleland-Hura"
        flavorText = "Whether for good or ill, their arrival always means change."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/258217df-ae88-4d93-895a-3fd242baacd1.jpg?1562902554"
    }
}
