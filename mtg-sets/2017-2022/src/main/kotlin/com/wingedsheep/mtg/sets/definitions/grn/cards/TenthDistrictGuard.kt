package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tenth District Guard
 * {1}{W}
 * Creature — Human Soldier
 * 2/2
 * When this creature enters, target creature gets +0/+1 until end of turn.
 */
val TenthDistrictGuard = card("Tenth District Guard") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "When this creature enters, target creature gets +0/+1 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(0, 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Craig J Spearing"
        flavorText = "\"The Tenth has always been my home. This city is constantly embroiled in one crisis or another, but I'm determined to protect my piece.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/2/829f959e-91cd-42ea-8644-ce828a304d01.jpg?1783934193"
    }
}
