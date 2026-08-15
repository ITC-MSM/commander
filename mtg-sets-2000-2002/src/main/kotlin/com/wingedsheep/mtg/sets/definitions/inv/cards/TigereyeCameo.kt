package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Tigereye Cameo
 * {3}
 * Artifact
 *
 * {T}: Add {G} or {W}.
 */
val TigereyeCameo = card("Tigereye Cameo") {
    manaCost = "{3}"
    colorIdentity = "GW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {G} or {W}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "314"
        artist = "Donato Giancola"
        flavorText = "\"An elvish adventurer unearthed this stone in Jolrael's jungle. Now it's truly fit for display in one of her palaces.\"\n—Isel, master carver"
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25976da8-338d-4f46-b8ea-78a0aa3daa35.jpg?1562902582"
    }
}
