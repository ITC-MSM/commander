package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Tattered Ratter
 * {1}{R}
 * Creature — Human Peasant
 * 2/2
 *
 * Whenever a Rat you control becomes blocked, it gets +2/+0 until end of turn.
 *
 * The Berserk Murlodont shape: an ANY-bound `becomesBlocked` watching every Rat *you control*
 * (not just this creature), with the pump applied to [EffectTarget.TriggeringEntity] — the Rat
 * that got blocked, which may well not be the Ratter. Untargeted, so a hexproof Rat is still
 * pumped. The trigger fires once per Rat that becomes blocked, and once only per combat for that
 * Rat regardless of how many creatures block it (CR 509.1h) — the +2/+0 doesn't scale with the
 * number of blockers.
 */
val TatteredRatter = card("Tattered Ratter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Peasant"
    power = 2
    toughness = 2
    oracleText = "Whenever a Rat you control becomes blocked, it gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.becomesBlocked(
            filter = GameObjectFilter.Creature.withSubtype("Rat").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.ModifyStats(2, 0, EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "152"
        artist = "Tyler Walpole"
        flavorText = "\"What's that, Snappers? Yes, you're right! It *was* very rude of the " +
            "innkeeper to throw us out!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30f505b4-d61c-4da8-ab45-37125260d556.jpg?1783915087"
    }
}
