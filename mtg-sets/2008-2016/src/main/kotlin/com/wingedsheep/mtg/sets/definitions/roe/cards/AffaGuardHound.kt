package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Affa Guard Hound
 * {2}{W}
 * Creature — Dog
 * 2/2
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * When this creature enters, target creature gets +0/+3 until end of turn.
 */
val AffaGuardHound = card("Affa Guard Hound") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\nWhen this creature enters, target creature gets +0/+3 until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(0, 3, creature)
        description = "When this creature enters, target creature gets +0/+3 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Ryan Pancoast"
        flavorText = "Once a welcoming hub for explorers, Affa became a place of guarded tongues and quick defenses."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85623f54-1c18-4b1e-a8da-df66de3832a6.jpg?1783942011"
    }
}
