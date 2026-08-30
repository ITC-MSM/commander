package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Seeds of Strength
 * {G}{W}
 * Instant
 * Target creature gets +1/+1 until end of turn.
 * Target creature gets +1/+1 until end of turn.
 * Target creature gets +1/+1 until end of turn.
 *
 * Three independent "target creature" requirements, so the same creature may legally be chosen more
 * than once for a cumulative +2/+2 or +3/+3 (Scryfall ruling, 2005-10-01).
 */
val SeedsOfStrength = card("Seeds of Strength") {
    manaCost = "{G}{W}"
    colorIdentity = "WG"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+1 until end of turn.\n" +
        "Target creature gets +1/+1 until end of turn.\n" +
        "Target creature gets +1/+1 until end of turn."
    spell {
        val first = target("first", TargetCreature(filter = TargetFilter.Creature))
        val second = target("second", TargetCreature(filter = TargetFilter.Creature))
        val third = target("third", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, first),
            Effects.ModifyStats(1, 1, second),
            Effects.ModifyStats(1, 1, third),
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "227"
        artist = "Ralph Horsley"
        flavorText = "Beneath the beauty of light and seed is the might of Vitu-Ghazi."
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfa1ac13-adbd-439a-90b2-9c506aec0836.jpg"
        ruling(
            "2005-10-01",
            "You may choose the same creature as a target multiple times since the card says " +
                "“target creature” multiple times. You may give three different creatures " +
                "+1/+1 each, one creature +2/+2 and another creature +1/+1, or a single creature +3/+3.",
        )
    }
}
