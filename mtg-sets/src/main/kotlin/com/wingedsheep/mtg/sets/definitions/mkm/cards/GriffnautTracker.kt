package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Griffnaut Tracker
 * {3}{W}
 * Creature — Human Detective
 * 3/2
 *
 * Flying
 * When this creature enters, exile up to two target cards from a single graveyard.
 */
val GriffnautTracker = card("Griffnaut Tracker") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Detective"
    power = 3
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, exile up to two target cards from a single graveyard."

    keywords(Keyword.FLYING)

    // ETB: exile up to two target cards, both from the same graveyard (sameOwner) —
    // the Arashin Sunshield shape.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to two target cards from a single graveyard",
            TargetObject(
                count = 2,
                optional = true,
                filter = TargetFilter.CardInGraveyard,
                sameOwner = true,
            )
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Svetlin Velinov"
        flavorText = "\"The desperate will tread unexpected paths. Be prepared to follow.\"\n—Tam Sennic, Ezrim's second-in-command"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95f5d048-226f-49a4-a2ce-a6fa99aa9e8a.jpg?1783912926"
    }
}
