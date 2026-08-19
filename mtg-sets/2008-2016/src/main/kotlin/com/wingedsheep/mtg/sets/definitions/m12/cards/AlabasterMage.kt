package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alabaster Mage
 * {1}{W}
 * Creature — Human Wizard
 * 2/1
 *
 * {1}{W}: Target creature you control gains lifelink until end of turn. (Damage dealt by the creature also causes its controller to gain that much life.)
 */
val AlabasterMage = card("Alabaster Mage") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    oracleText = "{1}{W}: Target creature you control gains lifelink until end of turn. (Damage dealt by the creature also causes its controller to gain that much life.)"
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.GrantKeyword(Keyword.LIFELINK, creature)
        description = "{1}{W}: Target creature you control gains lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Izzy"
        flavorText = "\"We hold sacred the powers of light and life. Truth and honor are our greatest weapons.\"\n—Alabaster creed"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f82e6a81-6a45-45f9-829d-332859a32257.jpg?1783941106"
    }
}
