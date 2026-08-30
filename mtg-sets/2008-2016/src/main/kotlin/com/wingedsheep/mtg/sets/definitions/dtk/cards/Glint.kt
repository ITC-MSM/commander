package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Glint
 * {1}{U}
 * Instant
 *
 * Target creature you control gets +0/+3 and gains hexproof until end of turn. (It can't be the target of spells or abilities your opponents control.)
 *
 * "you control" is a controller predicate on the requirement, not a clause on the effect, so it
 * rides [Targets.CreatureYouControl]. Hexproof is granted as a plain keyword —
 * `Effects.GrantHexproof` exists for the player-targeting case, which this card never reaches.
 */
val Glint = card("Glint") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +0/+3 and gains hexproof until end of turn. (It can't be the target of spells or abilities your opponents control.)"

    spell {
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(0, 3, t),
            Effects.GrantKeyword(Keyword.HEXPROOF, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Igor Kieryluk"
        flavorText = "Rakshasa waste no opportunity to display their wealth and power, even in the midst of a sorcerous duel."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35fa105f-c76b-4fd0-9b83-34b46648e0d6.jpg?1783938608"
    }
}
