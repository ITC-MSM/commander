package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crash the Ramparts
 * {2}{G}
 * Instant
 *
 * Target creature gets +3/+3 and gains trample until end of turn.
 */
val CrashTheRamparts = card("Crash the Ramparts") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+3 and gains trample until end of turn."

    spell {
        val boosted = target("target", Targets.Creature)
        effect = Effects.ModifyStats(3, 3, boosted) then
            Effects.GrantKeyword(Keyword.TRAMPLE, boosted)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Mark Behm"
        flavorText = "The Legion's conquistadors could endure Ixalan's sun. Their forts could withstand a charging ceratops. But nothing can stop a ceratops strengthened by the sun."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20b52d17-7efb-4c6e-9e6a-4763d1ef1daa.jpg"
    }
}
