package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val MemoryTrap = card("Memory Trap") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, exile target nonland permanent an opponent controls until this enchantment leaves the battlefield."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target("nonland permanent an opponent controls",
            TargetObject(filter = TargetFilter.NonlandPermanentOpponentControls))
        effect = Effects.MoveUntilSourceLeaves(permanent, Zone.EXILE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Ekaterina Burmak"
        flavorText = "The Theorist used his own memories to trap the parts of Jace that objected to his plans, and to ensnare the friends who wished to stop him."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f5345ae-4489-4d05-b2d5-c71285254f05.jpg?1788866058"
        inBooster = false
    }
}
