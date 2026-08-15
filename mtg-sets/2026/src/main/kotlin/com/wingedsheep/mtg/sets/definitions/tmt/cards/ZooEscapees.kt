package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zoo Escapees
 * {1}{G}
 * Creature — Boar Rhino
 * 2/2
 *
 * When this creature leaves the battlefield, create a Mutagen token. (It's an
 * artifact with "{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target
 * creature. Activate only as a sorcery.")
 */
val ZooEscapees = card("Zoo Escapees") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar Rhino"
    oracleText = "When this creature leaves the battlefield, create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.CreateMutagenToken()
        description = "When this creature leaves the battlefield, create a Mutagen token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Mirko Failoni"
        flavorText = "They imagine that out beyond the zoo, beyond the pen, there exists another world, and it encroaches with rapid certainty upon their own . . ."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f57ba8ff-2f8c-4ca1-9b13-42a7cc213e99.jpg?1771502750"
    }
}
