package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Mocking Sprite
 * {2}{U}
 * Creature — Faerie Rogue
 * 2/1
 * Flying
 * Instant and sorcery spells you cast cost {1} less to cast.
 */
val MockingSprite = card("Mocking Sprite") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Rogue"
    power = 2
    toughness = 1
    oracleText = "Flying\nInstant and sorcery spells you cast cost {1} less to cast."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.InstantOrSorcery),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Ben Hill"
        flavorText = "\"Look at me, everyone! I'm a High Fae! Watch me do important High Fae things!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e595014d-4ff4-4561-b7f2-a9bd56300b01.jpg?1782696613"
    }
}
