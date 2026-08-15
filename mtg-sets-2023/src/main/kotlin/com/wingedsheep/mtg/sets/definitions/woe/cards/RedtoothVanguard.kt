package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Redtooth Vanguard
 * {1}{G}
 * Creature — Elf Warrior
 * 3/1
 *
 * Trample
 * Whenever an enchantment you control enters, you may pay {2}. If you do, return this card from
 * your graveyard to your hand.
 *
 * The recursion ability functions only while this card is in the graveyard (`triggerZone =
 * GRAVEYARD`), same shape as Dragon Shadow's graveyard-resident enters trigger. `MayPayManaEffect`
 * models "you may pay {2}. If you do, ..." and the inner Move returns the source from the graveyard
 * to its owner's hand.
 */
val RedtoothVanguard = card("Redtooth Vanguard") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 3
    toughness = 1
    oracleText = "Trample\nWhenever an enchantment you control enters, you may pay {2}. If you do, " +
        "return this card from your graveyard to your hand."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY
        )
        triggerZone = Zone.GRAVEYARD
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = Effects.Move(EffectTarget.Self, Zone.HAND, fromZone = Zone.GRAVEYARD)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Joshua Cairos"
        flavorText = "\"I'm just as deadly during the day.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55271960-b9bd-4bea-93ca-3321bf30be78.jpg?1783915079"
    }
}
