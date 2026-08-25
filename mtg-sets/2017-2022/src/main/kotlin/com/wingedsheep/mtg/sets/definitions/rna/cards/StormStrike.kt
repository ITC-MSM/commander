package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Strike — Ravnica Allegiance #119
 * {R} · Instant
 *
 * The pump and the first-strike grant are one clause about one creature, so they nest inside a
 * single composite; the scry is the separate trailing sentence.
 */
val StormStrike = card("Storm Strike") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 and gains first strike until end of turn. Scry 1."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(listOf(
            Effects.Composite(listOf(
                Effects.ModifyStats(1, 0, creature),
                Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature)
            )),
            Effects.Scry(1)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Dmitry Burmak"
        flavorText = "\"My shout is thunder and my fist is lightning!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8de24cba-545a-438b-9516-1c19a50ca78c.jpg"
    }
}
