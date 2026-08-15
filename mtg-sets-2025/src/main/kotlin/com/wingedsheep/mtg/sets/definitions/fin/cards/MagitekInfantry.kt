package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Magitek Infantry
 * {W}
 * Artifact Creature — Robot Soldier
 * 1/1
 *
 * This creature gets +1/+0 as long as you control another artifact.
 * {2}{W}: Search your library for a card named Magitek Infantry, put it onto the
 * battlefield tapped, then shuffle.
 */
val MagitekInfantry = card("Magitek Infantry") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Robot Soldier"
    power = 1
    toughness = 1
    oracleText = "This creature gets +1/+0 as long as you control another artifact.\n" +
        "{2}{W}: Search your library for a card named Magitek Infantry, put it onto the battlefield tapped, then shuffle."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 1, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.YouControl(GameObjectFilter.Artifact, excludeSelf = true)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{2}{W}")
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.named("Magitek Infantry"),
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
            shuffleAfter = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "John Tyler Christopher"
        flavorText = "These cybersoldiers were specifically engineered to excel in combat, and serve as the powerhouse of the imperial infantry."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b64dc6d7-dd01-4e66-9099-4c90865448df.jpg?1748705847"
    }
}
