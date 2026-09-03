package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Colfenor's Urn
 * {3} Artifact
 *
 * Whenever a creature with toughness 4 or greater is put into your graveyard from the
 * battlefield, you may exile it.
 * At the beginning of the end step, if three or more cards have been exiled with this
 * artifact, sacrifice it. If you do, return those cards to the battlefield under their
 * owner's control.
 *
 * Both halves are the linked-exile pile: the first ability feeds it, the second empties it.
 *
 * - **"into *your* graveyard"** is an ownership clause, not a control one — a creature you own
 *   that an opponent has stolen still dies into your graveyard — so the filter is
 *   `ownedByYou()`, never `youControl()`.
 * - **Toughness is read from last-known information.** The creature is already in the graveyard
 *   when the trigger is checked; `TriggerMatcher` threads `lastKnownToughness` off the
 *   `ZoneChangeEvent` for battlefield departures, so `toughnessAtLeast(4)` measures the creature
 *   as it last existed on the battlefield (a 4-toughness creature killed by -4/-4 does not
 *   trigger; one pumped to 4 does).
 * - **A token triggers but can't be exiled** (2007-10-01 ruling): it ceases to exist as a state
 *   based action before the trigger resolves, so the move finds nothing. That falls out of the
 *   engine's own zone handling — no special case here.
 * - **The end-step ability is a mandatory sacrifice with an intervening "if"** (CR 603.4), so it
 *   only triggers at three or more cards and re-checks on resolution. `IfYouDo` gates the return
 *   on the sacrifice actually happening: the 2007-10-01 ruling is explicit that an Urn which left
 *   the battlefield or changed controllers returns nothing. `SuccessCriterion.PermanentsSacrificed`
 *   is what makes that fail-closed — the same shape as Safe Haven's upkeep ability.
 * - The count is over the **whole game**, not the turn: `LINKED_EXILE_CARD_COUNT` reads the pile
 *   itself, which is exactly that.
 */
val ColfenorsUrn = card("Colfenor's Urn") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a creature with toughness 4 or greater is put into your graveyard " +
        "from the battlefield, you may exile it.\n" +
        "At the beginning of the end step, if three or more cards have been exiled with this " +
        "artifact, sacrifice it. If you do, return those cards to the battlefield under their " +
        "owner's control."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.toughnessAtLeast(4).ownedByYou(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        optional = true
        effect = Effects.Move(
            EffectTarget.TriggeringEntity,
            Zone.EXILE,
            fromZone = Zone.GRAVEYARD,
            linkToSource = true
        )
        description = "Whenever a creature with toughness 4 or greater is put into your " +
            "graveyard from the battlefield, you may exile it."
    }

    triggeredAbility {
        trigger = Triggers.EachEndStep
        interveningIf = Compare(
            DynamicAmount.ContextProperty(ContextPropertyKey.LINKED_EXILE_CARD_COUNT),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(3)
        )
        effect = Effects.IfYouDo(
            action = Effects.SacrificeTarget(EffectTarget.Self),
            ifYouDo = Effects.ReturnLinkedExileUnderOwnersControl(),
            successCriterion = SuccessCriterion.PermanentsSacrificed
        )
        description = "At the beginning of the end step, if three or more cards have been " +
            "exiled with this artifact, sacrifice it. If you do, return those cards to the " +
            "battlefield under their owner's control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "254"
        artist = "Jim Pavelec"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/419fea28-3813-4e65-8b90-6d335fdf0a0b.jpg?1783942851"
        ruling("2007-10-01", "The second ability checks how many creatures have been exiled with Colfenor's Urn over the course of the entire game.")
        ruling("2007-10-01", "The second ability forces you to sacrifice Colfenor's Urn. If the ability triggers and the Urn isn't sacrificed (because it left the battlefield or changed controllers, for example), the exiled cards won't be returned to the battlefield.")
        ruling("2007-10-01", "The first ability triggers when a token creature with toughness 4 or greater is put into your graveyard. However, that token will cease to exist before Colfenor's Urn can exile it.")
    }
}
