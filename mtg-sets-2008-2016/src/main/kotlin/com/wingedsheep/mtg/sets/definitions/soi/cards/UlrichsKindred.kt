package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ulrich's Kindred
 * {2}{R}
 * Creature — Wolf
 * 3/2
 * Trample
 * {3}{G}: Target attacking Wolf or Werewolf gains indestructible until end of turn.
 */
val UlrichsKindred = card("Ulrich's Kindred") {
    manaCost = "{2}{R}"
    colorIdentity = "RG"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 2
    oracleText = "Trample\n{3}{G}: Target attacking Wolf or Werewolf gains indestructible until " +
        "end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        target = TargetObject(
            filter = TargetFilter(
                GameObjectFilter.Creature.attacking().withAnySubtype("Wolf", "Werewolf")
            )
        )
        effect = Effects.GrantKeyword(
            Keyword.INDESTRUCTIBLE,
            EffectTarget.ContextTarget(0),
            Duration.EndOfTurn
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "187"
        artist = "Josu Hernaiz"
        flavorText = "\"I am not its master. We are of the same pack.\"\n—Ulrich, Krallenhorde alpha"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86d0ee03-a8c6-4f37-9885-60a26a2e2728.jpg?1782712029"
    }
}
