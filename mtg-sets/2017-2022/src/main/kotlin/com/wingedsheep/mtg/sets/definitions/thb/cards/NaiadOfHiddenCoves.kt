package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Naiad of Hidden Coves
 * {2}{U}
 * Enchantment Creature — Nymph
 * 2/3
 *
 * During turns other than yours, spells you cast cost {1} less to cast.
 *
 * The "during turns other than yours" clause folds into [ModifySpellCost]'s own `gating` slot rather
 * than wrapping the ability in a `ConditionalStaticAbility`: cost calculation scans the battlefield
 * for bare `is ModifySpellCost` statics and never consults the layer system, so a conditional
 * wrapper would hide the ability and the reduction would silently never apply.
 */
val NaiadOfHiddenCoves = card("Naiad of Hidden Coves") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Nymph"
    power = 2
    toughness = 3
    oracleText = "During turns other than yours, spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(Conditions.IsNotYourTurn),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Kieran Yanner"
        flavorText = "\"Wave-borne, he watches over secrets of the shore.\"\n—Psemilla, Meletian poet"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dc6ae489-658b-4c12-b204-f7b58ce84375.jpg"
    }
}
