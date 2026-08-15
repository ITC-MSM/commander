package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Foot Elite
 * {2}{W/B}
 * Creature — Human Ninja
 * 2/4
 *
 * Whenever this creature attacks, another target creature you control
 * gets +1/+0 and gains indestructible until end of turn.
 */
val FootElite = card("Foot Elite") {
    manaCost = "{2}{W/B}"
    colorIdentity = "WB"
    typeLine = "Creature — Human Ninja"
    oracleText = "Whenever this creature attacks, another target creature you control gets +1/+0 and gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "another target creature you control",
            TargetPermanent(filter = TargetFilter.CreatureYouControl.other())
        )
        effect = Effects.ModifyStats(1, 0, creature)
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature, Duration.EndOfTurn))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Zoltan Boros"
        flavorText = "The Foot Elite are the personal students of the Shredder, a reserve guard without equal."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24bd571e-652a-4e7c-afc6-a45f0ccf62f6.jpg?1771342404"
    }
}
