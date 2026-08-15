package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Drudge Sentinel
 * {2}{B}
 * Creature — Skeleton Warrior
 * 2/1
 * {3}: Tap this creature. It gains indestructible until end of turn.
 */
val DrudgeSentinel = card("Drudge Sentinel") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton Warrior"
    power = 2
    toughness = 1
    oracleText = "{3}: Tap this creature. It gains indestructible until end of turn."

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.Composite(
            Effects.Tap(EffectTarget.Self),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Sara Winters"
        flavorText = "The Cabal assured the Seven Houses that hostages receive all the food and rest they require."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/9764a718-1748-4c2a-925c-370e22003b62.jpg?1562739890"
    }
}
