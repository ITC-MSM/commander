package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Magnetic Snuffler — Murders at Karlov Manor #254
 * {5} · Artifact Creature — Construct · 4/4 · Uncommon
 *
 * When this creature enters, return target Equipment card from your graveyard to the battlefield
 * attached to this creature.
 * Whenever you sacrifice an artifact, put a +1/+1 counter on this creature.
 *
 * A colorless five-drop that re-buys a dead Equipment and suits up in the same breath, then grows
 * off the set's artifact-sacrifice theme (Clues especially — every Clue cracked for a card is also a
 * counter here).
 *
 * **The reanimate-and-attach half** is a two-step composite rather than
 * [Effects.PutOntoBattlefieldAttachedToChosen]: the printed text names a *fixed* host ("attached to
 * this creature"), not a chosen one, so the chosen-host effect would open a decision with exactly
 * one legal answer. Instead the Equipment is put onto the battlefield and then attached with
 * [Effects.AttachTargetEquipmentToCreature], whose `creatureTarget` is [EffectTarget.Self].
 *
 * That ordering also reproduces the card's one ruling exactly. You *may* target an Equipment that
 * can't legally be attached to the Snuffler — a "equip only to Human creatures" Equipment, say, or
 * the case where the Snuffler has already left the battlefield in response to its own trigger. The
 * Equipment still enters (the first step is unconditional); the attachment then either never happens
 * or is undone by the CR 704.5n state-based action, leaving it on the battlefield unattached. That
 * is what the ruling prescribes, and it falls out of the composite without a special case.
 *
 * **The counter half** is [Triggers.YouSacrificeA] — the per-permanent shape, not the batched
 * [Triggers.YouSacrificeOneOrMore] — because "whenever you sacrifice an artifact" fires once per
 * artifact, so sacrificing three Clues to one effect puts three counters on. `YouSacrificeA` counts
 * the source sacrificing itself, which is correct here: the Snuffler is itself an artifact, and
 * sacrificing it does trigger the ability (the counter is simply moot, since the permanent that
 * would receive it is gone).
 */
val MagneticSnuffler = card("Magnetic Snuffler") {
    manaCost = "{5}"
    typeLine = "Artifact Creature — Construct"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, return target Equipment card from your graveyard to " +
        "the battlefield attached to this creature.\n" +
        "Whenever you sacrifice an artifact, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val equipment = target(
            "target Equipment card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Artifact
                        .withSubtype(Subtype.EQUIPMENT)
                        .ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOntoBattlefieldUnderYourControl(equipment)
            .then(
                Effects.AttachTargetEquipmentToCreature(
                    equipmentTarget = equipment,
                    creatureTarget = EffectTarget.Self
                )
            )
        description = "When this creature enters, return target Equipment card from your " +
            "graveyard to the battlefield attached to this creature."
    }

    triggeredAbility {
        trigger = Triggers.YouSacrificeA(GameObjectFilter.Artifact)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you sacrifice an artifact, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "254"
        artist = "Daniel Ljunggren"
        flavorText = "It recovers precious jewelry, murder weapons, and discarded food tins with " +
            "equal excitement."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70476534-2fc7-4872-a009-3380dd5ce2ab.jpg?1783912830"

        ruling(
            "2024-02-02",
            "You may target an Equipment card that can't legally be attached to Magnetic Snuffler " +
                "with its first ability. If you do, it will enter the battlefield unattached."
        )
    }
}
