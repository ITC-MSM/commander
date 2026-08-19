package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ironroot Warlord
 * {1}{G}{W}
 * Creature — Treefolk Soldier
 * &#42;/5
 *
 * Ironroot Warlord's power is equal to the number of creatures you control.
 * {3}{G}{W}: Create a 1/1 white Soldier creature token.
 */
val IronrootWarlord = card("Ironroot Warlord") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Treefolk Soldier"
    oracleText = "Ironroot Warlord's power is equal to the number of creatures you control.\n{3}{G}{W}: Create a 1/1 white Soldier creature token."
    toughness = 5

    dynamicPower(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature))

    activatedAbility {
        cost = Costs.Mana("{3}{G}{W}")
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
        )
        description = "{3}{G}{W}: Create a 1/1 white Soldier creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Filip Burburan"
        flavorText = "\"Alone, it's a fortification. At the head of its troops, it's a battering ram.\"\n—Skerk Hobnett, wilderness guide"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edec85ce-7daa-48c2-b25d-b22941e01e73.jpg?1783932951"
    }
}
