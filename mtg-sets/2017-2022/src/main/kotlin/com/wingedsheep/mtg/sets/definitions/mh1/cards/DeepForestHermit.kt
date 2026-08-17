package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Deep Forest Hermit
 * {3}{G}{G}
 * Creature — Elf Druid
 * 1/1
 *
 * Vanishing 3
 * When this creature enters, create four 1/1 green Squirrel creature tokens.
 * Squirrels you control get +1/+1.
 *
 * Vanishing is declared, not spelled out: the engine supplies all three of CR 702.62's abilities
 * from the keyword — see [com.wingedsheep.sdk.scripting.Vanishing].
 *
 * "Squirrels you control" carries no card type, so it is every Squirrel *permanent* you control —
 * [GameObjectFilter.Permanent], not `.Creature`. The Hermit is an Elf Druid, so it never pumps
 * itself and no `excludeSelf` is needed.
 */
val DeepForestHermit = card("Deep Forest Hermit") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "Vanishing 3 (This creature enters with three time counters on it. At the " +
        "beginning of your upkeep, remove a time counter from it. When the last is removed, " +
        "sacrifice it.)\n" +
        "When this creature enters, create four 1/1 green Squirrel creature tokens.\n" +
        "Squirrels you control get +1/+1."
    power = 1
    toughness = 1

    keywordAbility(KeywordAbility.vanishing(3))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Squirrel"),
            count = 4
        )
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.SQUIRREL).youControl()
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "161"
        artist = "Chris Seaman"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/3287775f-7bec-4e8f-bb8d-daf5ce92e4a8.jpg?1783933100"
    }
}
