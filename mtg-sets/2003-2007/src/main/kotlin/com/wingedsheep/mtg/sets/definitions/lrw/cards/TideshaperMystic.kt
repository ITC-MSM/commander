package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.OptionType

/**
 * Tideshaper Mystic
 * {U}
 * Creature — Merfolk Wizard
 * 1/1
 * {T}: Target land becomes the basic land type of your choice until end of turn.
 * Activate only during your turn.
 *
 * Dream Thrush's ability with a timing restriction bolted on: `ChooseOption(BASIC_LAND_TYPE)`
 * records the pick, then [Effects.SetLandType] installs the Layer-4 (TYPE) effect that *replaces*
 * the land's existing subtypes (CR 305.7) — so the land loses its old mana abilities and gains the
 * chosen type's, which is what makes this a Merfolk islandwalk enabler and a colour-screw tool at
 * once.
 */
val TideshaperMystic = card("Tideshaper Mystic") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "{T}: Target land becomes the basic land type of your choice until end of turn. " +
        "Activate only during your turn."

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
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        description = "{T}: Target land becomes the basic land type of your choice until end of turn. " +
            "Activate only during your turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Mark Tedin"
        flavorText = "He paints with drop and shimmer a world that exists only in the wistful heart."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e526ebbf-2041-4c3e-9a20-73bbf6f90251.jpg?1783942896"
    }
}
