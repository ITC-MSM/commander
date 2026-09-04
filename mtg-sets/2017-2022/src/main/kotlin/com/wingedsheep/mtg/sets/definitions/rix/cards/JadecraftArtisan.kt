package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jadecraft Artisan
 * {3}{G}
 * Creature — Merfolk Shaman
 * 3/3
 * When this creature enters, target creature gets +2/+2 until end of turn.
 */
val JadecraftArtisan = card("Jadecraft Artisan") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Shaman"
    oracleText = "When this creature enters, target creature gets +2/+2 until end of turn."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val boosted = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, boosted)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Izzy"
        flavorText = "\"A blade is not fully forged until it is given.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b745934-c1fe-49f5-bda4-b0eafa1408e1.jpg?1783935285"
    }
}
