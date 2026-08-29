package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kinsbaile Balloonist
 * {3}{W}
 * Creature — Kithkin Soldier
 * 2/2
 * Flying
 * Whenever this creature attacks, you may have target creature gain flying until end of turn.
 */
val KinsbaileBalloonist = card("Kinsbaile Balloonist") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 2
    toughness = 2
    oracleText = "Flying\nWhenever this creature attacks, you may have target creature gain flying until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        optional = true
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
        description = "you may have target creature gain flying until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Even when a giant's tantrum turns the sky into a chaotic gale, the path of the balloonist " +
            "never falters."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6053c20-3632-43e2-8560-259d0c12f235.jpg?1783942913"
    }
}
