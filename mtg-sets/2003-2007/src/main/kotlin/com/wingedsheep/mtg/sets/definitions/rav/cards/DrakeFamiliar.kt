package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Drake Familiar
 * {1}{U}
 * Creature — Drake
 * 2/1
 * Flying
 * When this creature enters, sacrifice it unless you return an enchantment to its owner's hand.
 *
 * The bounce is deliberately *not* scoped to enchantments you control: the 2005-10-01 ruling says
 * any enchantment on the battlefield qualifies, an opponent's included, and that an untargetable
 * one does too because the ability never targets. Hence `youControl = false`.
 */
val DrakeFamiliar = card("Drake Familiar") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying\nWhen this creature enters, sacrifice it unless you return an enchantment to its owner's hand."
    power = 2
    toughness = 1
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(
            cost = Costs.pay.ReturnToHand(filter = GameObjectFilter.Enchantment, youControl = false),
            suffer = SacrificeSelfEffect
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Darrell Riche"
        flavorText = "\"Falconry? A fine sport I suppose, if you're attracted to the frailty of birds.\"\n—Trivaz, Izzet mage"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4583623-e367-48cc-8e86-e6c5e35f1a9c.jpg?1783943688"
        ruling(
            "2005-10-01",
            "The ability lets you return any enchantment on the battlefield, including an opponent's " +
                "enchantment. The ability isn't targeted, so you can return an untargetable enchantment. " +
                "If there are no enchantments on the battlefield, or you choose not to return one, you " +
                "must sacrifice Drake Familiar."
        )
    }
}
