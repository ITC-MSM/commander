package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Briarpack Alpha
 * {3}{G}
 * Creature — Wolf
 * 3/3
 * Flash (You may cast this spell any time you could cast an instant.)
 * When this creature enters, target creature gets +2/+2 until end of turn.
 */
val BriarpackAlpha = card("Briarpack Alpha") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 3
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\nWhen this creature enters, target creature gets +2/+2 until end of turn."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "Daarken"
        flavorText = "\"The wolves turned on us and a chill swept over me. The pack had a new leader.\" —Alena, trapper of Kessig"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a052e945-7535-4b0a-b580-cf76377633f3.jpg?1783940811"
    }
}
