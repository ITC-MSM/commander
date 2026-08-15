package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Spider-Byte, Web Warden
 * {2}{U}
 * Legendary Creature — Spider Avatar Hero, 2/2
 * When Spider-Byte enters, return up to one target nonland permanent to its owner's hand.
 */
val SpiderByteWebWarden = card("Spider-Byte, Web Warden") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Spider Avatar Hero"
    oracleText = "When Spider-Byte enters, return up to one target nonland permanent to its owner's hand."
    power = 2
    toughness = 2
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent))
        effect = Effects.Move(t, Zone.HAND)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "44"
        artist = "Thanh Tuấn"
        flavorText = "\"What better place for a virtual spider than a worldwide web?\"\n—Spider-Byte, Margo Kess"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/210ae606-12a4-453b-bfb4-73ca9c22b8b5.jpg?1757377023"
    }
}
