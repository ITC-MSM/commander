package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MustAttack
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ares, God of War — Marvel Super Heroes #202 (rare)
 * {1}{B}{R} · Legendary Creature — God Warrior Villain · 4/3
 *
 * Ares attacks each combat if able.
 * Whenever an attacking creature you control dies, return that card to its owner's hand.
 *
 * Implementation notes:
 * - "Attacks each combat if able" is the [MustAttack] static over [GroupFilter.source] — a
 *   requirement the declare-attackers legality check enforces, not an effect that taps Ares.
 * - The death trigger is a battlefield → graveyard zone change ([Triggers.leavesBattlefield] with
 *   `to = Zone.GRAVEYARD`) filtered to attacking creatures you control. The `attacking()` predicate
 *   reads the last-known-information snapshot (`EntitySnapshot.wasAttacking`) rather than live
 *   state — the creature has already left combat and the battlefield by the time the trigger is
 *   detected (CR 603.10) — the same mechanism Éomer, Marshal of Rohan relies on.
 * - [TriggerBinding.ANY], not `OTHER`: the printed text has no "another", so Ares dying while
 *   attacking returns *himself* to hand.
 * - "Return **that card**" is [EffectTarget.TriggeringEntity] with `fromZone = Zone.GRAVEYARD`.
 *   The `fromZone` guard is load-bearing, not decoration: it makes the move a no-op when the object
 *   is not (or no longer) in the graveyard, which is the correct null result for a token — it dies,
 *   ceases to exist, and there is no card to return — and for a card something else has already
 *   moved out of the graveyard in response.
 * - Nothing is optional and nothing targets, so the ability returns every qualifying card, one
 *   trigger per creature, even in a board wipe during combat.
 */
val AresGodOfWar = card("Ares, God of War") {
    manaCost = "{1}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — God Warrior Villain"
    power = 4
    toughness = 3
    oracleText = "Ares attacks each combat if able.\n" +
        "Whenever an attacking creature you control dies, return that card to its owner's hand."

    staticAbility {
        ability = MustAttack(GroupFilter.source())
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl().attacking(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Move(
            EffectTarget.TriggeringEntity,
            Zone.HAND,
            fromZone = Zone.GRAVEYARD,
        )
        description = "Whenever an attacking creature you control dies, return that card to its " +
            "owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "202"
        artist = "Fesbra"
        flavorText = "\"Let the slaughter begin!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e121d65-d341-40b9-bebc-1e7d1b83905c.jpg?1783902906"
    }
}
