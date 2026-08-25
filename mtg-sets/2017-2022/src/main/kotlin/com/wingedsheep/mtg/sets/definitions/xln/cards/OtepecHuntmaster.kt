package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Otepec Huntmaster
 * {1}{R}
 * Creature — Human Shaman
 * 1/2
 *
 * Dinosaur spells you cast cost {1} less to cast.
 * {T}: Target Dinosaur gains haste until end of turn.
 */
val OtepecHuntmaster = card("Otepec Huntmaster") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman"
    oracleText = "Dinosaur spells you cast cost {1} less to cast.\n" +
        "{T}: Target Dinosaur gains haste until end of turn."
    power = 1
    toughness = 2

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype.DINOSAUR)),
            modification = CostModification.ReduceGeneric(1)
        )
    }

    activatedAbility {
        cost = Costs.Tap
        val dino = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.DINOSAUR)))
        )
        effect = Effects.GrantKeyword(Keyword.HASTE, dino)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "153"
        artist = "Daarken"
        flavorText = "\"Forward! Let the Burning Sun's light guide you to deserving prey!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c334e6f3-1378-4429-b4f1-fa8ed7ab7123.jpg"
    }
}
