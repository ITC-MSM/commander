package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Separatist Voidmage
 * {3}{U}
 * Creature — Human Wizard
 * 2/2
 *
 * When this creature enters, you may return target creature to its owner's hand.
 */
val SeparatistVoidmage = card("Separatist Voidmage") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "When this creature enters, you may return target creature to its owner's hand."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target("target creature", Targets.Creature)
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Jason Rainville"
        flavorText = "\"As long as each side thinks it can win, the balance holds, and the mage-rings stand.\"\n" +
            "—Alhammarret"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5634d1a-ca4b-4528-9e0e-b88f1025d434.jpg"
    }
}
