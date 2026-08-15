package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mirkwood Spider
 * {G}
 * Creature — Spider
 * 1/1
 *
 * Deathtouch
 * Whenever this creature attacks, target legendary creature you control gains deathtouch until end of turn.
 */
val MirkwoodSpider = card("Mirkwood Spider") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 1
    toughness = 1
    oracleText = "Deathtouch\nWhenever this creature attacks, target legendary creature you control gains deathtouch until end of turn."

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.Attacks
        val legendary = target(
            "target legendary creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl.legendary())
        )
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, legendary)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Alexander Ostrowski"
        flavorText = "Far and wide Shelob's lesser broods spread from glen to glen, to Dol Guldur and the fastnesses of Mirkwood."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad961ba1-c74f-4a44-87fe-b30e2b63e378.jpg?1686969491"
    }
}
