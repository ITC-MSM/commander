package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wingnut, Bat on the Belfry
 * {1}{R}
 * Legendary Creature — Bat Mutant
 * 1/2
 *
 * Alliance — Whenever another creature you control enters, Wingnut
 * gains your choice of flying, menace, or haste until end of turn.
 * Whenever Wingnut attacks, each other attacking creature gets +1/+0
 * until end of turn.
 */
val WingnutBatOnTheBelfry = card("Wingnut, Bat on the Belfry") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Bat Mutant"
    oracleText = "Alliance — Whenever another creature you control enters, Wingnut gains your choice of flying, menace, or haste until end of turn.\nWhenever Wingnut attacks, each other attacking creature gets +1/+0 until end of turn."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = ModalEffect(
            modes = listOf(
                Mode.noTarget(
                    Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self, Duration.EndOfTurn),
                    "Wingnut gains flying until end of turn"
                ),
                Mode.noTarget(
                    Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self, Duration.EndOfTurn),
                    "Wingnut gains menace until end of turn"
                ),
                Mode.noTarget(
                    Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn),
                    "Wingnut gains haste until end of turn"
                ),
            ),
            chooseCount = 1,
            countsAsModalSpell = false
        )
        description = "Alliance — Whenever another creature you control enters, Wingnut gains your choice of flying, menace, or haste until end of turn."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(
                GameObjectFilter.Creature.attacking(),
                excludeSelf = true
            ),
            effect = ModifyStatsEffect(1, 0, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "110"
        artist = "Zoltan Boros"
        flavorText = "\"Tell me, Screwloose, aren't we good? Mmhm. So, wouldn't that make them evil? Right!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40b9b1d0-b7d1-473e-b6a9-a29d527c2f35.jpg?1771502679"
    }
}
