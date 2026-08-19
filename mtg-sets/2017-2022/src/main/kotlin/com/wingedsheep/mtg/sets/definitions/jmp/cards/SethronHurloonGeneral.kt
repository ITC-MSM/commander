package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sethron, Hurloon General
 * {3}{R}{R}
 * Legendary Creature — Minotaur Warrior
 * 4/4
 *
 * Whenever Sethron or another nontoken Minotaur you control enters, create a 2/3 red Minotaur creature token.
 * {2}{B/R}: Minotaurs you control get +1/+0 and gain menace and haste until end of turn. ({B/R} can be paid with either {B} or {R}.)
 */
val SethronHurloonGeneral = card("Sethron, Hurloon General") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Minotaur Warrior"
    oracleText = "Whenever Sethron or another nontoken Minotaur you control enters, create a 2/3 red Minotaur creature token.\n{2}{B/R}: Minotaurs you control get +1/+0 and gain menace and haste until end of turn. ({B/R} can be paid with either {B} or {R}.)"
    power = 4
    toughness = 4

    triggeredAbility {
        // "Sethron or another nontoken Minotaur you control" — the corpus spells this as one
        // ANY-bound trigger over the nontoken filter (Headless Rider's shape).
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.MINOTAUR).youControl().nontoken(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateToken(
            power = 2,
            toughness = 3,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Minotaur"),
        )
        description = "Whenever Sethron or another nontoken Minotaur you control enters, create a 2/3 " +
            "red Minotaur creature token."
    }

    activatedAbility {
        cost = Costs.Mana("{2}{B/R}")
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.MINOTAUR).youControl()),
            Effects.ModifyStats(1, 0, EffectTarget.Self)
                .then(Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self))
                .then(Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)),
        )
        description = "{2}{B/R}: Minotaurs you control get +1/+0 and gain menace and haste until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "25"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/274cdb39-1454-4c9b-acd8-4f762a48e71f.jpg?1783930501"
    }
}
