package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Take Up the Shield
 * {1}{W}
 * Instant
 *
 * Put a +1/+1 counter on target creature. It gains lifelink and indestructible until end of turn.
 * (Damage and effects that say "destroy" don't destroy it.)
 *
 * Canonical printing lives here in Dominaria United (earliest real-expansion printing). The
 * Outlaws of Thunder Junction reprint contributes a [com.wingedsheep.sdk.model.Printing] row only.
 */
val TakeUpTheShield = card("Take Up the Shield") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Put a +1/+1 counter on target creature. It gains lifelink and indestructible until end of turn. " +
        "(Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        val t = target("target creature", TargetCreature())
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
            .then(Effects.GrantKeyword(Keyword.LIFELINK, t))
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Manuel Castañón"
        flavorText = "\"You are all part of my pride. As long as I live, I will protect you.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/5/851e842e-a497-4c36-90ee-8d64f806c378.jpg?1673306597"
    }
}
