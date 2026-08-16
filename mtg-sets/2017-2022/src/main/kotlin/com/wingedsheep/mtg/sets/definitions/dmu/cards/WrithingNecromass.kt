package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Writhing Necromass
 * {6}{B}
 * Creature — Zombie Giant
 * 5/5
 * This spell costs {1} less to cast for each creature card in your graveyard.
 * Deathtouch
 */
val WrithingNecromass = card("Writhing Necromass") {
    manaCost = "{6}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Giant"
    oracleText = "This spell costs {1} less to cast for each creature card in your graveyard.\nDeathtouch"
    power = 5
    toughness = 5

    keywords(Keyword.DEATHTOUCH)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(
                    filter = GameObjectFilter.Creature,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Campbell White"
        flavorText = "\"We sent three of our best legions! You're telling me no one came back?\"\n—Tori D'Avenant"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a166abef-f068-47e9-8a65-43ebf4b015bd.jpg?1783921322"
    }
}
