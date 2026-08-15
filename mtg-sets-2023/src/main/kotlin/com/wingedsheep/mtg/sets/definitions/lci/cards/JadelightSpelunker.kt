package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RepeatDynamicTimesEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Jadelight Spelunker — {X}{G}
 * Creature — Merfolk Scout
 * 1/1
 * Rare — The Lost Caverns of Ixalan #196
 * Artist: Izzy
 *
 * "When this creature enters, it explores X times."
 *
 * X is the value paid for the spell's {X} generic mana, stamped onto the spell's xValue
 * and available in the ETB trigger context via [DynamicAmount.XValue] (CR 107.3a). The
 * trigger repeats the Explore mechanic (CR 701.44) exactly X times via
 * [RepeatDynamicTimesEffect]: each iteration reveals the top library card; a land goes to
 * hand, otherwise the Spelunker gets a +1/+1 counter and the player may put the card in
 * the graveyard. X=0 is legal and produces no explores.
 */
val JadelightSpelunker = card("Jadelight Spelunker") {
    manaCost = "{X}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Scout"
    oracleText = "When this creature enters, it explores X times. (To have it explore, reveal the top card of your library. Put that card into your hand if it's a land. Otherwise, put a +1/+1 counter on that creature, then put the card back or put it into your graveyard.)"
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = RepeatDynamicTimesEffect(
            amount = DynamicAmount.XValue,
            body = Effects.Explore(EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "196"
        artist = "Izzy"
        flavorText = "The River Heralds found clues to a grand heritage below Ixalan's surface."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68e633d3-47e2-48c5-9be3-574ce5023bf7.jpg?1782694451"
    }
}
