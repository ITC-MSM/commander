package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Featherbrained Filcher
 * {W}
 * Creature — Bird Mutant
 * 0/2
 *
 * Flying
 * When this creature leaves the battlefield, create a Food token.
 */
val FeatherbrainedFilcher = card("Featherbrained Filcher") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Mutant"
    oracleText = "Flying\nWhen this creature leaves the battlefield, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"
    power = 0
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Jakob Eirich"
        flavorText = "\"HI! I'm Pete!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78c55706-4d00-4b97-965c-0b2d0963e59d.jpg?1771513766"
    }
}
