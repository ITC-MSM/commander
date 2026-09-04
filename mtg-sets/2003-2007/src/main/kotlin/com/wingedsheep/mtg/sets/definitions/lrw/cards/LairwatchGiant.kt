package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanBlockAdditionalForCreatureGroup
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lairwatch Giant
 * {5}{W}
 * Creature — Giant Warrior
 * 5/3
 *
 * This creature can block an additional creature each combat.
 * Whenever this creature blocks two or more creatures, it gains first strike until end of turn.
 *
 * The two halves are one card: the first is what makes the second reachable, so the extra block is
 * [CanBlockAdditionalForCreatureGroup] scoped to [GroupFilter.source] (Selesnya Sagittars' shape).
 *
 * "Blocks two or more creatures" is a *count* over the blocks declared this combat, which is the
 * `minBlockedAttackers` bar on `BlockEvent` — the blocking mirror of
 * `CreaturesAttackYourOpponentEvent.minAttackers`. It deliberately fires **once** per combat rather
 * than once per blocked attacker: the printed wording is a single event, and the 2007-10-01 ruling
 * says an already-two-blocking Giant pushed to three by a later effect does *not* trigger again.
 *
 * Known limit, and it is the ruling's other half: the trigger is read off the
 * `BlockersDeclaredEvent`, so a Giant that blocks only one creature at declaration and is pushed to
 * two by a later effect will not trigger. The engine emits no event for a mid-combat change to the
 * block assignment, so there is nothing to detect on; closing it means a new event, not a new field.
 */
val LairwatchGiant = card("Lairwatch Giant") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant Warrior"
    power = 5
    toughness = 3
    oracleText = "This creature can block an additional creature each combat.\n" +
        "Whenever this creature blocks two or more creatures, it gains first strike until end of turn."

    staticAbility {
        ability = CanBlockAdditionalForCreatureGroup(
            count = 1,
            filter = GroupFilter.source(),
        )
    }

    triggeredAbility {
        trigger = Triggers.blocks(minBlockedAttackers = 2)
        effect = Effects.GrantKeyword(
            keyword = Keyword.FIRST_STRIKE,
            target = EffectTarget.Self,
            duration = Duration.EndOfTurn,
        )
        description = "it gains first strike until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Warren Mahy"
        flavorText = "A giant can brood over a grudge for decades, and woe to those who interrupt him."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c64ba1c8-b93a-4169-9ba0-d3fc5bf57676.jpg?1783942912"
        ruling("2007-10-01", "Lairwatch Giant's second ability triggers if it blocks at least two creatures when blockers are declared. It will also trigger if it blocks fewer than two creatures when blockers are declared and effects then cause it to block more creatures. However, if Lairwatch Giant is already blocking two or more creatures, and effects then cause it to block more creatures, the ability won't trigger again.")
    }
}
