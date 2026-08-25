package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Renegade Freighter
 * {3}
 * Artifact — Vehicle
 * 4/3
 * Whenever this Vehicle attacks, it gets +1/+1 and gains trample until end of turn.
 * Crew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes
 * an artifact creature until end of turn.)
 *
 * "This Vehicle" is just the source, so the attack trigger is the SELF-bound [Triggers.Attacks]
 * and both halves of its effect point at [EffectTarget.Self]. Crew is the engine's own
 * [KeywordAbility.crew] activated ability; the printed 4 / 3 only matters once it is crewed.
 */
val RenegadeFreighter = card("Renegade Freighter") {
    manaCost = "{3}"
    typeLine = "Artifact — Vehicle"
    oracleText = "Whenever this Vehicle attacks, it gets +1/+1 and gains trample until end of turn.\n" +
        "Crew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
        description = "It gets +1/+1 and gains trample until end of turn."
    }

    keywordAbility(KeywordAbility.crew(2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "230"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a10e2c3-0132-4eb2-94f0-5915caca2a17.jpg?1783937150"
    }
}
