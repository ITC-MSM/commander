package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Gideon's Reproach
 * {1}{W}
 * Instant
 * Gideon's Reproach deals 4 damage to target attacking or blocking creature.
 *
 * Battle for Zendikar (2015) is the earliest printing; Dominaria (2018), where the canonical used
 * to live, now carries a [com.wingedsheep.sdk.model.Printing] row instead.
 */
val GideonsReproach = card("Gideon's Reproach") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Gideon's Reproach deals 4 damage to target attacking or blocking creature."

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Dan Murayama Scott"
        flavorText = "\"Suddenly, Gideon was there with us, clearing the way so that we could escape.\"\n" +
            "—*The War Diaries*"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/453d8ec3-15ae-4851-9efb-161ca2ee6019.jpg?1783938218"
    }
}
