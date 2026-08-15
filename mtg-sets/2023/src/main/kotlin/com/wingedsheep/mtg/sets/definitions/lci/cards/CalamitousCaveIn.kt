package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Calamitous Cave-In — {3}{R}
 * Sorcery (LCI #139, uncommon)
 *
 * "Calamitous Cave-In deals X damage to each creature and each planeswalker,
 *  where X is the number of Caves you control plus the number of Cave cards
 *  in your graveyard."
 *
 * X is evaluated at resolution as the sum of:
 *   - Battlefield: permanents with the Cave land subtype you control
 *     ([DynamicAmount.Count] over [Zone.BATTLEFIELD] filtered by [GameObjectFilter.Land.withSubtype("Cave")])
 *   - Graveyard:   any card with the Cave subtype in your graveyard
 *     ([DynamicAmount.Count] over [Zone.GRAVEYARD] filtered by [GameObjectFilter.Any.withSubtype("Cave")])
 *
 * The two counts are summed via [DynamicAmount.Add] and fed into
 * [Patterns.Group.dealDamageToAll] over [GroupFilter]([GameObjectFilter.CreatureOrPlaneswalker]).
 */
val CalamitousCaveIn = card("Calamitous Cave-In") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Calamitous Cave-In deals X damage to each creature and each planeswalker, " +
        "where X is the number of Caves you control plus the number of Cave cards in your graveyard."

    spell {
        val cavesControlled = DynamicAmount.Count(
            player = Player.You,
            zone = Zone.BATTLEFIELD,
            filter = GameObjectFilter.Land.withSubtype("Cave"),
        )
        val cavesInGraveyard = DynamicAmount.Count(
            player = Player.You,
            zone = Zone.GRAVEYARD,
            filter = GameObjectFilter.Any.withSubtype("Cave"),
        )
        effect = Patterns.Group.dealDamageToAll(
            amount = DynamicAmount.Add(cavesControlled, cavesInGraveyard),
            filter = GroupFilter(GameObjectFilter.CreatureOrPlaneswalker),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "139"
        artist = "Diego Gisbert"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/8341ddd9-aac1-4773-b8ce-51e35f696263.jpg?1782694499"
    }
}
