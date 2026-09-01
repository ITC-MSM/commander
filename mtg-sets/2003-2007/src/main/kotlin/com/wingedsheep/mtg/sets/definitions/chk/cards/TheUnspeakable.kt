package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The Unspeakable
 * {6}{U}{U}{U}
 * Legendary Creature — Spirit
 * 6/7
 *
 * Flying, trample
 * Whenever The Unspeakable deals combat damage to a player, you may return target Arcane card from
 * your graveyard to your hand.
 *
 * Argentum Assay declines this one — its grammar has no rule for the noun phrase "an Arcane card" —
 * so it is authored straight from the printed text. The printed "you may" is the builder's
 * `optional = true` (which lowers to a `Gate.MayDecide` around the return), and the target is the
 * bare-noun-subtype graveyard spelling `CardInGraveyard.withSubtype(ARCANE).ownedByYou()`. A target
 * is still chosen when the trigger goes on the stack — the yes/no comes at resolution.
 */
val TheUnspeakable = card("The Unspeakable") {
    manaCost = "{6}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Spirit"
    power = 6
    toughness = 7
    oracleText = "Flying, trample\n" +
        "Whenever The Unspeakable deals combat damage to a player, you may return target Arcane " +
        "card from your graveyard to your hand."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        optional = true
        val t = target(
            "target",
            TargetObject(filter = TargetFilter.CardInGraveyard.withSubtype(Subtype.ARCANE).ownedByYou())
        )
        effect = Effects.Move(t, Zone.HAND)
        description = "Whenever The Unspeakable deals combat damage to a player, you may return " +
            "target Arcane card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "Khang Le"
        flavorText = "It is madness that drives men to seek forbidden knowledge, and madness has given it form."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/5212bd3e-e8d8-483e-871b-29fd378f817e.jpg?1783944319"
    }
}
