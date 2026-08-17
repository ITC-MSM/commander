package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Civic Gardener
 * {1}{G}
 * Creature — Human Citizen
 * 2 / 2
 * Whenever this creature attacks, untap target creature or land.
 */
val CivicGardener = card("Civic Gardener") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Citizen"
    oracleText = "Whenever this creature attacks, untap target creature or land."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target("target", TargetObject(filter = TargetFilter.CreatureOrLandPermanent))
        effect = Effects.Untap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Drew Baker"
        flavorText = "\"I've told you knuckleheads a hundred times: You can kill 'em here, but bury the bodies somewhere else. If you disturb my prized azaleas again, you'd better dig a second hole for yourself!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f58d39d7-62bf-43c8-97b3-0f9069af0e29.jpg?1783923105"
    }
}
