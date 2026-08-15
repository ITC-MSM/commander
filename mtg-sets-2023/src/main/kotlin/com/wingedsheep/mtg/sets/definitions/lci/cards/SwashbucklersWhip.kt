package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Swashbuckler's Whip
 * {1}
 * Artifact — Equipment
 * Equipped creature has reach, "{2}, {T}: Tap target artifact or creature," and
 * "{8}, {T}: Discover 10."
 * Equip {1}
 *
 * The {T} in each granted ability taps the equipped creature (the ability's source).
 * Discover 10 uses the equipped creature's controller's library.
 */
val SwashbucklersWhip = card("Swashbuckler's Whip") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has reach, \"{2}, {T}: Tap target artifact or creature,\" and " +
        "\"{8}, {T}: Discover 10.\"\nEquip {1}"

    staticAbility {
        ability = GrantKeyword(Keyword.REACH, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap),
                effect = Effects.Tap(EffectTarget.ContextTarget(0)),
                targetRequirements = listOf(
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Creature))
                ),
                descriptionOverride = "{2}, {T}: Tap target artifact or creature."
            ),
            filter = Filters.EquippedCreature
        )
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{8}"), Costs.Tap),
                effect = Effects.Discover(10),
                descriptionOverride = "{8}, {T}: Discover 10."
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "263"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24a85c52-e5b5-4d65-931c-eacc0cf0fb31.jpg?1782694401"
    }
}
