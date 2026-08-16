package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Leaf-Crowned Visionary
 * {G}{G}
 * Creature — Elf Druid
 * 1/1
 * Other Elves you control get +1/+1.
 * Whenever you cast an Elf spell, you may pay {G}. If you do, draw a card.
 */
val LeafCrownedVisionary = card("Leaf-Crowned Visionary") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "Other Elves you control get +1/+1.\nWhenever you cast an Elf spell, you may pay {G}. If you do, draw a card."
    power = 1
    toughness = 1

    // "Other Elves you control" is every Elf *permanent* you control — a kindred
    // enchantment counts — minus this creature.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype("Elf").youControl(),
                excludeSelf = true
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype("Elf"))
        effect = MayPayManaEffect(ManaCost.parse("{G}"), Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "167"
        artist = "Anna Steinbauer"
        flavorText = "\"The seedling you save today may be the sheltering boughs of generations to come.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa7bd814-9f88-4e01-932d-07ac1abc060e.jpg?1783921299"
    }
}
