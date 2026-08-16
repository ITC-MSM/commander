package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Aeronaut Tinkerer
 * {2}{U}
 * Creature — Human Artificer
 * 2/3
 * This creature has flying as long as you control an artifact. (It can't be blocked except by creatures with flying or reach.)
 *
 * A [ConditionalStaticAbility] wrapping [GrantKeyword](FLYING) over [GroupFilter.source()], gated by
 * an [Exists] check for an artifact you control — re-evaluated continuously in Layer 6, so flying
 * comes and goes with the artifact.
 */
val AeronautTinkerer = card("Aeronaut Tinkerer") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 3
    oracleText = "This creature has flying as long as you control an artifact. (It can't be blocked except by creatures with flying or reach.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Willian Murai"
        flavorText = "\"All tinkerers have their heads in the clouds. I don't intend to stop there.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e145e85d-1eaa-4ec6-9208-ca6491577302.jpg?1783939196"
    }
}
