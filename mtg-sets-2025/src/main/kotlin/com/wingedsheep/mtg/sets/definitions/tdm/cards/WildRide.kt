package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Wild Ride — Tarkir: Dragonstorm #132
 * {R} · Sorcery · Common
 *
 * Target creature gets +3/+0 and gains haste until end of turn.
 * Harmonize {4}{R} (You may cast this card from your graveyard for its harmonize cost. You may
 * tap a creature you control to reduce that cost by {X}, where X is its power. Then exile this spell.)
 */
val WildRide = card("Wild Ride") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Target creature gets +3/+0 and gains haste until end of turn.\n" +
        "Harmonize {4}{R} (You may cast this card from your graveyard for its harmonize cost. " +
        "You may tap a creature you control to reduce that cost by {X}, where X is its power. Then exile this spell.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(power = 3, toughness = 0, target = creature)
            .then(Effects.GrantKeyword(Keyword.HASTE, creature, Duration.EndOfTurn))
    }

    keywordAbility(KeywordAbility.harmonize("{4}{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Filipe Pagliuso"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abc8c6f5-6135-428e-8476-1751f82623f9.jpg?1743233780"
    }
}
