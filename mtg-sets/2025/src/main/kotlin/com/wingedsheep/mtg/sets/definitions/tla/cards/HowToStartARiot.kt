package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * How to Start a Riot
 * {2}{R}
 * Instant — Lesson
 *
 * Target creature gains menace until end of turn. (It can't be blocked except by two or more creatures.)
 * Creatures target player controls get +2/+0 until end of turn.
 */
val HowToStartARiot = card("How to Start a Riot") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant — Lesson"
    oracleText = "Target creature gains menace until end of turn. " +
        "(It can't be blocked except by two or more creatures.)\n" +
        "Creatures target player controls get +2/+0 until end of turn."

    spell {
        val creature = target("target creature", TargetCreature())
        val player = target("target player", TargetPlayer())
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.MENACE, creature),
            Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.targetPlayerControls(player)),
                effect = ModifyStatsEffect(2, 0, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Robin Olausson"
        flavorText = "\"Hey! RIOT!\""
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23b3bf1e-ea85-47f5-8473-4d16615f68d7.jpg?1764120961"
    }
}
