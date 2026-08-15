package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crustacean Commando
 * {1}{U}
 * Creature — Crab Mutant Soldier
 * 0/3
 *
 * When this creature enters, create a Mutagen token. (It's an artifact with
 * "{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature.
 *  Activate only as a sorcery.")
 */
val CrustaceanCommando = card("Crustacean Commando") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab Mutant Soldier"
    oracleText = "When this creature enters, create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"
    power = 0
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateMutagenToken()
        description = "When this creature enters, create a Mutagen token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Narendra Bintara Adi"
        flavorText = "\"Wondering what I've got in the box? Pain, soldier! And lots of it!\"\n—Herman"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/9528cc07-df4b-417f-ad65-c2fae6fc2d49.jpg?1771502563"
    }
}
