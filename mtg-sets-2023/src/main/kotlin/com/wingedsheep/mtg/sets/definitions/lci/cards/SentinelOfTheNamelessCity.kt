package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sentinel of the Nameless City — {2}{G}
 * Creature — Merfolk Warrior Scout
 * 3/4
 * Vigilance
 * Whenever this creature enters or attacks, create a Map token.
 * (It's an artifact with "{1}, {T}, Sacrifice this artifact: Target creature you control
 *  explores. Activate only as a sorcery.")
 */
val SentinelOfTheNamelessCity = card("Sentinel of the Nameless City") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Warrior Scout"
    oracleText = "Vigilance\nWhenever this creature enters or attacks, create a Map token. (It's an artifact with \"{1}, {T}, Sacrifice this artifact: Target creature you control explores. Activate only as a sorcery.\")"
    power = 3
    toughness = 4

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateMapToken()
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.CreateMapToken()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "211"
        artist = "Josu Hernaiz"
        flavorText = "\"Halt, traveler. Whatever you seek, you will not find it here.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eeeffc0b-dc92-458e-ad58-86ff6077a508.jpg?1699044484"
    }
}
