package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Gravblade Heavy
 * {3}{B}
 * Creature — Human Soldier
 * As long as you control an artifact, this creature gets +1/+0 and has deathtouch.
 */
val GravbladeHeavy = card("Gravblade Heavy") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 4
    oracleText = "As long as you control an artifact, this creature gets +1/+0 and has deathtouch."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(
                powerBonus = 1,
                toughnessBonus = 0,
                filter = GroupFilter.source()
            ),
            condition = Conditions.ControlArtifact
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, GroupFilter.source()),
            condition = Conditions.ControlArtifact
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Andrew Mar"
        flavorText = "It's difficult to dodge a weapon that commands gravity itself."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3872341-d711-407b-85e4-46ccb99988e1.jpg?1752946969"
    }
}
