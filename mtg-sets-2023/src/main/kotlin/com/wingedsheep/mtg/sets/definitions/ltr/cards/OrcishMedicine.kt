package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Orcish Medicine
 * {1}{B}
 * Instant
 *
 * Target creature gains your choice of lifelink or indestructible until end of turn.
 * Amass Orcs 1.
 *
 * Modeled as a choose-one between the two keyword grants; the amass rider rides along on the chosen
 * mode so the whole spell (including the amass) is countered if the single target becomes illegal.
 */
val OrcishMedicine = card("Orcish Medicine") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gains your choice of lifelink or indestructible until end of turn.\n" +
        "Amass Orcs 1. (Put a +1/+1 counter on an Army you control. It's also an Orc. If you don't " +
        "control an Army, create a 0/0 black Orc Army creature token first.)"

    spell {
        modal(chooseCount = 1) {
            mode("Target creature gains lifelink until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.LIFELINK, creature)
                    .then(Effects.Amass(1, "Orc"))
            }
            mode("Target creature gains indestructible until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature)
                    .then(Effects.Amass(1, "Orc"))
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Irvin Rodriguez"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66fae9ab-2302-4dea-a4e8-701938a0ef09.jpg?1686968679"
    }
}
