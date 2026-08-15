package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Protective Parents
 * {2}{W}
 * Creature — Human Peasant
 * 3/2
 *
 * When this creature dies, create a Young Hero Role token attached to up to one target creature you
 * control. (If you control another Role on it, put that one into the graveyard. Enchanted creature
 * has "Whenever this creature attacks, if its toughness is 3 or less, put a +1/+1 counter on it.")
 *
 * "Up to one target" is optional targeting, so the trigger still resolves with no target chosen —
 * it just creates nothing (WOE ruling). The Parents themselves are in the graveyard by the time the
 * trigger resolves, so they're never a legal target; the same optional-role shape as Cut In.
 */
val ProtectiveParents = card("Protective Parents") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Peasant"
    power = 3
    toughness = 2
    oracleText = "When this creature dies, create a Young Hero Role token attached to up to one " +
        "target creature you control. (If you control another Role on it, put that one into the " +
        "graveyard. Enchanted creature has \"Whenever this creature attacks, if its toughness is 3 " +
        "or less, put a +1/+1 counter on it.\")"

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target(
            "up to one target creature you control",
            TargetCreature(optional = true, filter = TargetFilter.CreatureYouControl)
        )
        effect = Effects.CreateRoleToken("Young Hero Role", t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68cc9653-80ef-4606-a0ec-6d4228fdb118.jpg?1783915130"

        ruling(
            "2023-09-01",
            "If you don't choose a target for Protective Parents's ability, the Young Hero Role token " +
                "won't be created."
        )
        ruling(
            "2023-09-01",
            "If a permanent has more than one Role attached to it controlled by the same player, each of " +
                "those Roles except the one with the most recent timestamp is put into its owner's graveyard. " +
                "This is a state-based action."
        )
    }
}
