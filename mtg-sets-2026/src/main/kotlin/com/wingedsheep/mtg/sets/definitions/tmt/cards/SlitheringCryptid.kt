package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Slithering Cryptid
 * {2}{G/U}
 * Creature — Fish Mutant
 * 2/3
 *
 * When this creature enters, create a Mutagen token. (It's an artifact with
 * "{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature.
 *  Activate only as a sorcery.")
 */
val SlitheringCryptid = card("Slithering Cryptid") {
    manaCost = "{2}{G/U}"
    colorIdentity = "GU"
    typeLine = "Creature — Fish Mutant"
    oracleText = "When this creature enters, create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateMutagenToken()
        description = "When this creature enters, create a Mutagen token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Nicholas Gregory"
        flavorText = "\"The Slithery! He's snatches little kids an' keeps 'em in cages! He don't got no legs and just slithers.\"\n—Lita"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d35cb39-8832-4cf1-be73-8de49fbea529.jpg?1771502794"
    }
}
