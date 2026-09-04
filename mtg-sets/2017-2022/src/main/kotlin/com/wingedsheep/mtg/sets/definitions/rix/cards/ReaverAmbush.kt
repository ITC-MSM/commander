package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Reaver Ambush
 * {2}{B}
 * Instant
 * Exile target creature with power 3 or less.
 */
val ReaverAmbush = card("Reaver Ambush") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature with power 3 or less."

    spell {
        val victim = target(
            "target creature with power 3 or less",
            Targets.CreatureWithPowerAtMost(3)
        )
        effect = Effects.Exile(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Sidharth Chaturvedi"
        flavorText = "Onora knew with sudden, grim certainty he would never see the Great River again."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fceaac2f-b26c-4da9-bf4f-c7672394dd7f.jpg?1783935308"
    }
}
