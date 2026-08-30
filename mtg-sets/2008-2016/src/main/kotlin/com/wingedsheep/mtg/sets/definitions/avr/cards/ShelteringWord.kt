package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sheltering Word
 * {1}{G}
 * Instant
 *
 * Target creature you control gains hexproof until end of turn. You gain life equal to that creature's toughness. (A creature with hexproof can't be the target of spells or abilities your opponents control.)
 *
 * The grant is the plain [Effects.GrantKeyword] floating-keyword shape rather than
 * `Effects.GrantHexproof` — the latter is the player-or-permanent evasion effect, a different
 * model. "That creature's toughness" is read at resolution off the spell's own target with
 * [DynamicAmounts.targetToughness].
 */
val ShelteringWord = card("Sheltering Word") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature you control gains hexproof until end of turn. You gain life equal to that " +
        "creature's toughness. (A creature with hexproof can't be the target of spells or abilities your " +
        "opponents control.)"

    spell {
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.HEXPROOF, creature),
            Effects.GainLife(DynamicAmounts.targetToughness())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93cd9be4-1ce4-4a7c-b2a6-98d3fde0a92b.jpg?1783940662"
    }
}
