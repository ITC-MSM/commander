package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blinding Mage — Magic 2010 #5
 * {1}{W} · Creature — Human Wizard · 1 / 2
 *
 * {W}, {T}: Tap target creature.
 *
 * The Gideon's Lawkeeper shape: a [Costs.Composite] of one mana atom and the tap symbol over
 * [Effects.Tap] on an unrestricted creature target — tapping an already-tapped creature is a legal
 * activation that simply does nothing.
 */
val BlindingMage = card("Blinding Mage") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 2
    oracleText = "{W}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Eric Deschamps"
        flavorText = "\"I carry the light of truth. Do not pity those it blinds, for they never had eyes to see.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/2/420de077-7ee0-446c-aefc-ca8d7ac698b5.jpg?1783942404"
    }
}
