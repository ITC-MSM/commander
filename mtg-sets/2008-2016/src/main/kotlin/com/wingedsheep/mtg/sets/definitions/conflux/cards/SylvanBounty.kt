package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Sylvan Bounty
 * {5}{G}
 * Instant
 * Target player gains 8 life.
 * Basic landcycling {1}{G}
 *
 * A single [Effects.GainLife] pointed at the bound player rather than at the default controller —
 * the printed line says "target player", so the recipient is the target slot, not "you".
 *
 * "Basic landcycling" is [KeywordAbility.basicLandcycling], the typecycling machinery narrowed to
 * *basic* land cards, matching the `IsBasicLand` search filter in the reminder text.
 */
val SylvanBounty = card("Sylvan Bounty") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target player gains 8 life.\n" +
        "Basic landcycling {1}{G} ({1}{G}, Discard this card: Search your library for a basic land " +
        "card, reveal it, put it into your hand, then shuffle.)"

    spell {
        val t = target("target", TargetPlayer())
        effect = Effects.GainLife(8, t)
    }

    keywordAbility(KeywordAbility.basicLandcycling("{1}{G}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Chris Rahn"
        flavorText = "Some who scouted new lands chose to stay."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f717c573-e448-400e-a228-d438491f1754.jpg"
    }
}
