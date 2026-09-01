package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Heat Shimmer
 * {2}{R}
 * Sorcery
 * Create a token that's a copy of target creature, except it has haste and
 * "At the beginning of the end step, exile this token."
 *
 * The exile sibling of Electroduplicate: the same `addedKeywords = HASTE` copy exception, but the
 * token is *exiled* rather than sacrificed, so it triggers no dies/leaves-the-battlefield
 * abilities of its own. `exileAtStep = Step.END` fires on the next end step of **any** player's
 * turn — the printed text says "the end step", not "your end step" — so a Heat Shimmer cast on an
 * opponent's turn loses the token that same turn.
 *
 * Unlike Electroduplicate, the target is any creature on the battlefield, not just one you
 * control: this is red's take on a Clone, and stealing the mould off an opponent's best creature
 * for one attack is the point.
 */
val HeatShimmer = card("Heat Shimmer") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create a token that's a copy of target creature, except it has haste and " +
        "\"At the beginning of the end step, exile this token.\""

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.CreateTokenCopyOfTarget(
            creature,
            addedKeywords = setOf(Keyword.HASTE),
            exileAtStep = Step.END
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "175"
        artist = "Franz Vohwinkel"
        flavorText = "\"Better to flare out than to gutter.\"\n—Flamekin expression"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a432470c-7f68-4429-970a-3da8eabcf0b8.jpg?1783942875"
    }
}
