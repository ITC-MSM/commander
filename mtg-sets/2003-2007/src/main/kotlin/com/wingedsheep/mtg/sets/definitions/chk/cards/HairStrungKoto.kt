package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Hair-Strung Koto
 * {6}
 * Artifact
 *
 * Tap an untapped creature you control: Target player mills a card.
 *
 * The cost is [Costs.TapPermanents], not [Costs.Tap] — the Koto itself is never tapped; tapping a
 * creature *as a cost* rather than paying the `{T}` symbol means summoning sickness never applies
 * (CR 302.6) and only untapped permanents may be chosen (CR 701.26a). "You control" and "untapped"
 * are carried by the atom, so the filter only has to say *creature*; the artifact is not itself a
 * legal choice because it isn't one. Same shape as `chk/cards/AzamiLadyOfScrolls.kt` and
 * `lrw/cards/DrownerOfSecrets.kt`.
 */
val HairStrungKoto = card("Hair-Strung Koto") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Tap an untapped creature you control: Target player mills a card."

    activatedAbility {
        cost = Costs.TapPermanents(count = 1, filter = GameObjectFilter.Creature)
        val player = target("target", Targets.Player)
        effect = Patterns.Library.mill(1, player)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "252"
        artist = "Rebecca Guay"
        flavorText = "\"The Kami War drove many members of Konda's court insane. As their spiritual world turned against them, so too did their minds turn from reality.\"\n—*The History of Kamigawa*"
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5354475-6a0b-4b1f-b94c-7c103d99e88b.jpg?1783944281"
    }
}
