package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Return to the Sewers
 * {3}{U}
 * Instant
 *
 * Target creature's owner puts it on their choice of the top or bottom of their
 * library. You create a Mutagen token.
 */
val ReturnToTheSewers = card("Return to the Sewers") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature's owner puts it on their choice of the top or bottom of their library. You create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.PutOnTopOrBottomOfLibrary(creature)
            .then(Effects.CreateMutagenToken())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Miklós Ligeti"
        flavorText = "\"We strike hard and fade away into the night.\"\n—Leonardo"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52e99505-17dc-4051-99f6-e23559fc6c95.jpg?1771586826"
    }
}
