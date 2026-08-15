package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pearl Lake Ancient
 * {5}{U}{U}
 * Creature — Leviathan
 * 6/7
 * Flash
 * This spell can't be countered.
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Return three lands you control to their owner's hand: Return Pearl Lake Ancient to its owner's hand.
 */
val PearlLakeAncient = card("Pearl Lake Ancient") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Leviathan"
    oracleText = "Flash\nThis spell can't be countered.\nProwess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\nReturn three lands you control to their owner's hand: Return Pearl Lake Ancient to its owner's hand."
    power = 6
    toughness = 7

    keywords(Keyword.FLASH)
    cantBeCountered = true
    prowess()

    activatedAbility {
        cost = Costs.ReturnToHand(Filters.Land, count = 3)
        effect = Effects.ReturnToHand(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "49"
        artist = "Richard Wright"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbee4d2f-6b41-4717-a1c6-831034452bec.jpg?1562796500"
    }
}
