package com.wingedsheep.mtg.sets.definitions.scg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Dragonspeaker Shaman
 * {1}{R}{R}
 * Creature — Human Barbarian Shaman
 * 2/2
 * Dragon spells you cast cost {2} less to cast.
 */
val DragonspeakerShaman = card("Dragonspeaker Shaman") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Barbarian Shaman"
    power = 2
    toughness = 2
    oracleText = "Dragon spells you cast cost {2} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype("Dragon")),
            modification = CostModification.ReduceGeneric(2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Kev Walker"
        flavorText = "\"We speak the dragons' language of flame and rage. They speak our language of fury and honor. Together we shall weave a tale of destruction without equal.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/9/49f5fa96-dcfb-4d29-bea9-7dd99e8c43d8.jpg?1562528528"
    }
}
