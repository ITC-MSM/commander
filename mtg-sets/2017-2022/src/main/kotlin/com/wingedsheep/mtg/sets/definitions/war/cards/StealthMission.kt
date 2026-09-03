package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stealth Mission
 * {2}{U}
 * Sorcery
 * Put two +1/+1 counters on target creature you control. That creature can't be blocked this turn.
 *
 * "Can't be blocked this turn" is [Effects.GrantKeyword] over
 * [AbilityFlag.CANT_BE_BLOCKED] — the same grant "gains flying until end of turn" builds. The flag
 * rather than a [com.wingedsheep.sdk.core.Keyword] because "can't be blocked" names a whole
 * sentence, not a noun a creature can be said to gain. Both halves read the one named target so
 * "that creature" is the creature the counters went on.
 */
val StealthMission = card("Stealth Mission") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Put two +1/+1 counters on target creature you control. That creature can't be blocked this turn."

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature),
            Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Heonhwa"
        flavorText = "\"What they don't know will definitely hurt them.\"\n—Lazav"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7ddfd4f5-7fe1-4a22-9a85-bd9b75b16380.jpg"
    }
}
