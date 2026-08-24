package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crowd's Favor
 * {R}
 * Instant
 * Convoke
 * Target creature gets +1/+0 and gains first strike until end of turn.
 */
val CrowdsFavor = card("Crowd's Favor") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Target creature gets +1/+0 and gains first strike until end of turn. (It deals combat damage before creatures without first strike.)"

    keywords(Keyword.CONVOKE)

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, t)
            .then(Effects.GrantKeyword(Keyword.FIRST_STRIKE, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/536b8104-9d8d-444b-8535-62bcbe279de2.jpg?1783939175"
    }
}
