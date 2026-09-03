package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Battlefield Promotion
 * {1}{W}
 * Instant
 * Put a +1/+1 counter on target creature. That creature gains first strike until end of turn. You gain 2 life. (A creature with first strike deals combat damage before creatures without first strike.)
 *
 * All three halves resolve off one named target, so "that creature" is the creature the counter
 * went on. The counter is permanent; the first strike is a [Effects.GrantKeyword] that expires at
 * end of turn (the facade's default duration). The lifegain has no target — it is always the
 * controller.
 */
val BattlefieldPromotion = card("Battlefield Promotion") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Put a +1/+1 counter on target creature. That creature gains first strike until end of turn. You gain 2 life. (A creature with first strike deals combat damage before creatures without first strike.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Scott Murphy"
        flavorText = "\"Welcome to the Legion. You saved a district—now let's go save the world.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89f8e970-b90c-4829-8970-0a3364027bbb.jpg"
    }
}
