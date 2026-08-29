package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dromad Purebred
 * {4}{W}
 * Creature — Camel Beast
 * 1/5
 * Whenever this creature is dealt damage, you gain 1 life.
 *
 * A flat 1 life however much damage arrived — [Triggers.TakesDamage] with a fixed amount, not the
 * `TRIGGER_DAMAGE_AMOUNT` context property Sunhome Enforcer reads.
 */
val DromadPurebred = card("Dromad Purebred") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Camel Beast"
    oracleText = "Whenever this creature is dealt damage, you gain 1 life."
    power = 1
    toughness = 5

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Carl Critchlow"
        flavorText = "\"I have seen much from the back of my dromad, most of it terribly wrong. The more I see, the more I am convinced of the rightness of my path.\"\n—Heruj, Selesnya initiate"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/0106caf1-2201-4661-96a5-56af02963fa6.jpg"
    }
}
