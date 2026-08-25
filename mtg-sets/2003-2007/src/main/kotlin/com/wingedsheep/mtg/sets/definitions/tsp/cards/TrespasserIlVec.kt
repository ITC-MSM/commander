package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trespasser il-Vec
 * {2}{B}
 * Creature — Human Rogue
 * 3/1
 * Discard a card: This creature gains shadow until end of turn. (It can block or be blocked by
 * only creatures with shadow.)
 *
 * A granted [Keyword.SHADOW] is read by the block-evasion rules exactly like a printed one, so the
 * activation is a plain until-end-of-turn keyword grant onto itself — no separate evasion static.
 * Note the grant cuts both ways: while it is up the Trespasser also can't block non-shadow
 * creatures.
 */
val TrespasserIlVec = card("Trespasser il-Vec") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    power = 3
    toughness = 1
    oracleText = "Discard a card: This creature gains shadow until end of turn. (It can block or be blocked by only creatures with shadow.)"

    activatedAbility {
        cost = Costs.DiscardCard
        effect = Effects.GrantKeyword(Keyword.SHADOW, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Jim Murray"
        flavorText = "At the epicenter of the Rathi overlay, the City of Traitors did not align with Dominaria. Many of its inhabitants were caught between the two worlds."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/4903171e-76b2-437d-8965-e871e47a3482.jpg"
    }
}
