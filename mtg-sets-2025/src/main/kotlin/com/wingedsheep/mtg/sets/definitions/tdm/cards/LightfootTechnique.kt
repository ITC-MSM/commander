package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Lightfoot Technique
 * {1}{W}
 * Instant
 * Put a +1/+1 counter on target creature. It gains flying and indestructible until end of turn.
 */
val LightfootTechnique = card("Lightfoot Technique") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Put a +1/+1 counter on target creature. It gains flying and indestructible until end of turn. " +
        "(Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
            .then(Effects.GrantKeyword(Keyword.FLYING, t))
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Craig J Spearing"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/baac1a41-d44d-4184-9147-b4233e73de65.jpg?1743204007"
    }
}
