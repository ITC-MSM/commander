package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Adelbert Steiner
 * {1}{W}
 * Legendary Creature — Human Knight
 * 2/1
 * Lifelink
 * Adelbert Steiner gets +1/+1 for each Equipment you control.
 *
 * Modeling: lifelink is an intrinsic keyword; the self-buff is a continuous static
 * ([GrantDynamicStatsEffect] scoped to [GroupFilter.source]) whose bonus recomputes
 * from a live count of Equipment you control ([DynamicAmount.Count]).
 */
val AdelbertSteiner = card("Adelbert Steiner") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Knight"
    power = 2
    toughness = 1
    oracleText = "Lifelink\nAdelbert Steiner gets +1/+1 for each Equipment you control."

    keywords(Keyword.LIFELINK)

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmount.Count(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT).youControl()
            ),
            toughnessBonus = DynamicAmount.Count(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT).youControl()
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "3"
        artist = "Lorenzo Mastroianni"
        flavorText = "\"Chivalry requires a knight to look after his comrades-in-arms. I will not abandon you!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a67a991-1e52-4676-a2e3-2bc7aa943ab3.jpg?1748705765"
    }
}
