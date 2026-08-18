package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Illicit Masquerade — Murders at Karlov Manor #88
 *
 * The dies trigger's counter filter is evaluated from the zone-change event's last-known counters.
 * Its optional graveyard target excludes that triggering creature, which is still in the graveyard
 * when targets are chosen. On resolution the dead creature is exiled first, then the target is
 * returned; if the target has become illegal, normal target legality makes the whole ability fizzle
 * before the exile can happen.
 */
val IllicitMasquerade = card("Illicit Masquerade") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Flash\n" +
        "When this enchantment enters, put an impostor counter on each creature you control.\n" +
        "Whenever a creature you control with an impostor counter on it dies, exile it. Return " +
        "up to one other target creature card from your graveyard to the battlefield."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.AddCounters(IMPOSTOR_COUNTER, 1, EffectTarget.Self),
        )
        description = "When this enchantment enters, put an impostor counter on each creature " +
            "you control."
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl().withCounter(IMPOSTOR_COUNTER),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        val replacement = target(
            "up to one other target creature card from your graveyard",
            TargetObject(
                optional = true,
                filter = TargetFilter.CreatureInYourGraveyard.otherThanTriggeringEntity(),
            ),
        )
        effect = Effects.Move(EffectTarget.TriggeringEntity, Zone.EXILE, fromZone = Zone.GRAVEYARD)
            .then(Effects.Move(replacement, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD))
        description = "Whenever a creature you control with an impostor counter on it dies, " +
            "exile it. Return up to one other target creature card from your graveyard to the " +
            "battlefield."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Valera Lutfullina"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a7a3ec4-afaa-45e1-8cde-f15bf4bd7379.jpg?1783912896"

        ruling(
            "2024-02-02",
            "Illicit Masquerade's last ability affects all creatures you control with impostor " +
                "counters on them, not just ones that had impostor counters put on them with " +
                "Illicit Masquerade's second ability.",
        )
    }
}

private const val IMPOSTOR_COUNTER = "IMPOSTOR"
