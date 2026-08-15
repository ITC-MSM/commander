package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * High-Flying Ace
 * {2}{W}
 * Creature — Bird Mutant
 * 2/3
 *
 * Flying
 * {3}{W}: Target creature without flying gains flying until end of turn.
 * Activate only as a sorcery.
 */
val HighFlyingAce = card("High-Flying Ace") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Mutant"
    oracleText = "Flying\n{3}{W}: Target creature without flying gains flying until end of turn. Activate only as a sorcery."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        timing = TimingRule.SorcerySpeed
        val creature = target(
            "creature without flying",
            TargetPermanent(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING)),
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, creature, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Daniel Romanovsky"
        flavorText = "\"Nice to meet you, Botticelli . . . or whatever your name is. Tell your boss to send some scarier goons next time.\"\n—Ace Duck"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e927eb0b-1c69-421a-8acd-01e92f729ffb.jpg?1771502498"
    }
}
