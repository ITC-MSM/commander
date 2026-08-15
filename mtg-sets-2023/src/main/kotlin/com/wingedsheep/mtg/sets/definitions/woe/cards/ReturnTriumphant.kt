package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Return Triumphant
 * {1}{W}
 * Sorcery
 *
 * Return target creature card with mana value 3 or less from your graveyard to the battlefield.
 * Create a Young Hero Role token attached to it.
 *
 * "Attached to it" is the creature this spell just reanimated, not a second target — the Role
 * rides along on the same [target] handle. Entity identity survives the graveyard → battlefield
 * move, so the reanimation and the attach can be sequenced in one [Effects.Composite]: by the
 * time [Effects.CreateRoleToken] runs, the card is already a permanent and a legal host.
 *
 * The single target is mandatory (not "up to one"), so with no legal creature card in your
 * graveyard the spell can't be cast, and a target that leaves the graveyard in response fizzles
 * the whole spell — no Role either.
 */
val ReturnTriumphant = card("Return Triumphant") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return target creature card with mana value 3 or less from your graveyard to " +
        "the battlefield. Create a Young Hero Role token attached to it. (Enchanted creature has " +
        "\"Whenever this creature attacks, if its toughness is 3 or less, put a +1/+1 counter on " +
        "it.\" If you put another Role on the creature later, put this one into the graveyard.)"

    spell {
        val t = target(
            "target creature card with mana value 3 or less from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature.ownedByYou().manaValueAtMost(3),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.Composite(
            Effects.PutOntoBattlefield(t),
            Effects.CreateRoleToken("Young Hero Role", t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Will Gist"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5209c13-9591-48eb-8d6c-112b3bdd429a.jpg?1783915128"

        ruling(
            "2023-09-01",
            "If a permanent has more than one Role attached to it controlled by the same player, each of " +
                "those Roles except the one with the most recent timestamp is put into its owner's graveyard. " +
                "This is a state-based action."
        )
        ruling(
            "2023-09-01",
            "Some spells and abilities that create Role tokens require targets. If each target chosen is an " +
                "illegal target as that spell or ability tries to resolve, it won't resolve. The Role token " +
                "won't be created."
        )
    }
}
