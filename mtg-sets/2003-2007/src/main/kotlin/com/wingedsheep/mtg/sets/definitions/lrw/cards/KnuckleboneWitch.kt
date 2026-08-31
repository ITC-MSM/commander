package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Knucklebone Witch
 * {B}
 * Creature — Goblin Shaman
 * 1/1
 * Whenever a Goblin you control is put into a graveyard from the battlefield, you may put a
 * +1/+1 counter on this creature.
 *
 * The bare tribal noun "Goblin" names every permanent with the subtype, not only the creatures, so
 * the filter is [GameObjectFilter.Permanent]. There is no "another" here — the Witch is itself a
 * Goblin and does trigger on its own death, which is why the binding is [TriggerBinding.ANY]; by
 * the time that copy of the trigger resolves the Witch is gone and the counter has nowhere to go.
 *
 * "A graveyard", not "your graveyard": a Goblin you control but don't own still triggers this on
 * the way to its owner's graveyard, so the filter reads control and the destination is unqualified.
 */
val KnuckleboneWitch = card("Knucklebone Witch") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Shaman"
    power = 1
    toughness = 1
    oracleText = "Whenever a Goblin you control is put into a graveyard from the battlefield, " +
        "you may put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN).youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        optional = true
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "you may put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "120"
        artist = "Jim Pavelec"
        flavorText = "Each bone honors its owner's best pranks."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d66b355-9aff-4cce-ae4a-42b233475dcf.jpg?1783942889"
    }
}
