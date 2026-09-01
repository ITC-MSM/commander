package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.OptionType
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Terraformer
 * {2}{U}
 * Creature — Human Wizard
 * 2/2
 * {1}: Choose a basic land type. Each land you control becomes that type until end of turn.
 *
 * The untargeted, whole-board sibling of
 * [com.wingedsheep.mtg.sets.definitions.inv.cards.DreamThrush]: the same
 * `ChooseOption(BASIC_LAND_TYPE) → SetLandType(fromChosenValueKey = …)` pair, with the single
 * target swapped for a [Effects.ForEachInGroup] over the lands you control — inside that loop
 * `EffectTarget.Self` is the land being iterated.
 *
 * "Becomes" replaces the land's existing land subtypes (CR 305.7), so each land loses the mana
 * abilities of its old types and gains the chosen type's — [Effects.SetLandType], not the
 * additive `AddSubtype`. The group is snapshotted before the first iteration, and the chosen
 * type is picked once, up front, for every land.
 */
val Terraformer = card("Terraformer") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{1}: Choose a basic land type. Each land you control becomes that type until end of turn."

    val chosenKey = "chosenLandType"

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.Composite(
            Effects.ChooseOption(
                optionType = OptionType.BASIC_LAND_TYPE,
                storeAs = chosenKey
            ),
            Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Land.youControl()),
                effect = Effects.SetLandType(
                    target = EffectTarget.Self,
                    duration = Duration.EndOfTurn,
                    fromChosenValueKey = chosenKey
                )
            )
        )
        description = "{1}: Choose a basic land type. Each land you control becomes that type until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Luca Zontini"
        flavorText = "\"This feels a little more like home.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0b9d4f3-4570-4fe0-857f-97f5bd6ead44.jpg"
    }
}
