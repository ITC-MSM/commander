package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wing It
 * {1}{W}
 * Instant
 *
 * Target creature gets +2/+2 until end of turn. Put a flying counter on it. Scry 1.
 *
 * One target, three sentences. "It" in the second sentence is the same chosen creature, so the pump
 * and the [Counters.FLYING] keyword counter both take the *same* target handle rather than each
 * declaring one. The scry is not targeted at all — it's the spell's controller scrying — so it's
 * just a third step of the same resolution. Because there's a single target, an illegal target on
 * resolution counters the whole spell (CR 608.2b) and the scry doesn't happen either; that's
 * correct, and it's why all three live inside one spell body.
 */
val WingIt = card("Wing It") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. Put a flying counter on it. Scry 1."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature)
            .then(Effects.AddCounters(Counters.FLYING, 1, creature))
            .then(Patterns.Library.scry(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Christina Kraus"
        flavorText = "After a moment's confusion, the otter splashed through clouds and gamboled " +
            "in the wind, delighting in its newfound freedom."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e23abf47-22fa-4dca-bd42-fafa1c8b592f.jpg?1783911295"
    }
}
