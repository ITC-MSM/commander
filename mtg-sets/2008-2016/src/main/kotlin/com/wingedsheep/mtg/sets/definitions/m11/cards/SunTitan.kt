package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sun Titan
 * {4}{W}{W}
 * Creature — Giant
 * 6/6
 *
 * Vigilance
 * Whenever this creature enters or attacks, you may return target permanent card with mana value 3 or less from your graveyard to the battlefield.
 *
 * - "Enters **or** attacks" is not a single trigger event: it is two triggered abilities sharing one
 *   effect, each with its own target requirement (the same shape Haliya, Ascendant Cadet uses).
 * - The return is [Effects.PutOntoBattlefieldFromGraveyard], the guarded sibling of
 *   `PutOntoBattlefield` — a targeted graveyard-to-battlefield return keeps the `fromZone` guard.
 */
val SunTitan = card("Sun Titan") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant"
    power = 6
    toughness = 6
    oracleText = "Vigilance\n" +
        "Whenever this creature enters or attacks, you may return target permanent card with mana value 3 or less from your graveyard to the battlefield."

    keywords(Keyword.VIGILANCE)

    val returnDescription = "Whenever this creature enters or attacks, you may return target " +
        "permanent card with mana value 3 or less from your graveyard to the battlefield."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanentCard = target(
            "permanent card with mana value 3 or less from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.ownedByYou().manaValueAtMost(3),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(permanentCard)
        optional = true
        description = returnDescription
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val permanentCard = target(
            "permanent card with mana value 3 or less from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.ownedByYou().manaValueAtMost(3),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(permanentCard)
        optional = true
        description = returnDescription
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "35"
        artist = "Todd Lockwood"
        flavorText = "A blazing sun that never sets."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8db2b8e-dce9-49b7-833f-381ee55288cb.jpg?1783941831"
    }
}
