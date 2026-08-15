package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Witch's Mark
 * {1}{R}
 * Sorcery
 *
 * You may discard a card. If you do, draw two cards.
 * Create a Wicked Role token attached to up to one target creature you control.
 *
 * Two independent halves, and only the second one targets. "Up to one target" is optional
 * targeting, so the spell is castable with no target at all — you then just loot (2023-09-01
 * ruling). If you *do* pick a target and it's illegal on resolution, the spell doesn't resolve
 * and **none** of its effects happen: no discard, no draw, no Role. That all-or-nothing fizzle
 * is the engine's ordinary targeting behavior (CR 608.2b), so the two halves can sit in a plain
 * [Effects.Composite] rather than being separated.
 *
 * The loot half is the [MayEffect] + [IfYouDoEffect] shape: declining the "may" skips the draw,
 * and so does an empty hand — the discard accomplishes nothing, so the `ifYouDo` never fires.
 * The one-Role-per-creature rule (a state-based action) lives behind [Effects.CreateRoleToken].
 */
val WitchsMark = card("Witch's Mark") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "You may discard a card. If you do, draw two cards.\n" +
        "Create a Wicked Role token attached to up to one target creature you control. (If you " +
        "control another Role on it, put that one into the graveyard. Enchanted creature gets " +
        "+1/+1. When this token is put into a graveyard, each opponent loses 1 life.)"

    spell {
        val t = target(
            "up to one target creature you control",
            TargetCreature(optional = true, filter = TargetFilter.CreatureYouControl)
        )
        effect = Effects.Composite(
            MayEffect(
                effect = IfYouDoEffect(
                    action = Patterns.Hand.discardCards(1),
                    ifYouDo = Effects.DrawCards(2)
                ),
                descriptionOverride = "You may discard a card. If you do, draw two cards."
            ),
            Effects.CreateRoleToken("Wicked Role", t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Justyna Dura"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/0685afcb-06f6-4d18-b8c2-510764558dc1.jpg?1783915086"

        ruling(
            "2023-09-01",
            "You can cast Witch's Mark without a target just to discard a card and draw two cards. " +
                "However, if you do choose a target, and that target is illegal at the time Witch's Mark " +
                "tries to resolve, the spell won't resolve and none of its effects will happen. You won't " +
                "discard, draw, or create a Wicked Role token."
        )
        ruling(
            "2023-09-01",
            "If you don't choose a target for Witch's Mark, the Wicked Role token won't be created."
        )
        ruling(
            "2023-09-01",
            "If a permanent has more than one Role attached to it controlled by the same player, each of " +
                "those Roles except the one with the most recent timestamp is put into its owner's graveyard. " +
                "This is a state-based action."
        )
    }
}
