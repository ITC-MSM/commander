package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked

/**
 * Urborg Emissary
 * {2}{B}
 * Creature — Human Wizard
 * 3/1
 * Kicker {1}{U} (You may pay an additional {1}{U} as you cast this spell.)
 * When this creature enters, if it was kicked, return target permanent to its owner's hand.
 */
val UrborgEmissary = card("Urborg Emissary") {
    manaCost = "{2}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 1
    oracleText = "Kicker {1}{U} (You may pay an additional {1}{U} as you cast this spell.)\n" +
        "When this creature enters, if it was kicked, return target permanent to its owner's hand."

    keywordAbility(KeywordAbility.kicker("{1}{U}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        val t = target("permanent", Targets.Permanent)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "131"
        artist = "Eric Peterson"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6912c71-1836-4e87-9a65-d577d903d03c.jpg?1562941302"
    }
}
