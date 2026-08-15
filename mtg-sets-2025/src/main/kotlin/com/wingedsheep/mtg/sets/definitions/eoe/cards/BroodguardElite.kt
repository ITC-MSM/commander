package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Broodguard Elite
 * {X}{G}{G}
 * Creature — Insect Knight
 * 0/0
 * This creature enters with X +1/+1 counters on it.
 * When this creature leaves the battlefield, put its counters on target creature you control.
 * Warp {X}{G}
 *
 * The leaves-the-battlefield ability moves *all* counter kinds (including any -1/-1 counters),
 * reading the snapshot captured when the creature left the battlefield — per the WotC rulings,
 * it does not literally move the counters but places an equal number of each kind on the target.
 */
val BroodguardElite = card("Broodguard Elite") {
    manaCost = "{X}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Knight"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it.\n" +
        "When this creature leaves the battlefield, put its counters on target creature you control.\n" +
        "Warp {X}{G} (You may cast this card from your hand for its warp cost. Exile this creature at " +
        "the beginning of the next end step, then you may cast it from exile on a later turn.)"

    // Enters with X +1/+1 counters, where X is the value paid in its mana cost.
    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    // When this creature leaves the battlefield, put its counters on target creature you control.
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        target = Targets.CreatureYouControl
        effect = Effects.MoveAllLastKnownCounters(EffectTarget.ContextTarget(0))
    }

    warp = "{X}{G}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08b1d019-65ab-4dea-9076-041fd6338a35.jpg?1752947266"
        ruling(
            "2025-07-25",
            "Broodguard Elite's second ability doesn't cause you to move counters from Broodguard " +
                "Elite onto the target creature. Rather, you put the same number of each kind of " +
                "counter Broodguard Elite had when it left the battlefield onto the target creature."
        )
        ruling(
            "2025-07-25",
            "Broodguard Elite's second ability puts all counters that were on Broodguard Elite onto " +
                "the target creature, not just its +1/+1 counters. If it had -1/-1 counters, those " +
                "are included as well, which may result in the recipient dying."
        )
    }
}
