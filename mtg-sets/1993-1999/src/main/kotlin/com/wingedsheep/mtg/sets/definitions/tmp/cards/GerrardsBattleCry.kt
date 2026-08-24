package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gerrard's Battle Cry
 * {W}
 * Enchantment
 * {2}{W}: Creatures you control get +1/+1 until end of turn.
 */
val GerrardsBattleCry = card("Gerrard's Battle Cry") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "{2}{W}: Creatures you control get +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}{W}")
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Val Mayerik"
        flavorText = "Gerrard grinned and drew his sword. \"This won't be a fair fight,\" he called to his crew. \"They should have brought a second ship!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/0/504950d5-2df7-4518-b987-fe3a57ad1c58.jpg"
    }
}
