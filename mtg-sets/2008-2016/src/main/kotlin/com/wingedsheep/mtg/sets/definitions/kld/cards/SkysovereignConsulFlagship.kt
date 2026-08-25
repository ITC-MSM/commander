package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Skysovereign, Consul Flagship
 * {5}
 * Legendary Artifact — Vehicle
 * 6/5
 *
 * Flying
 * Whenever Skysovereign enters or attacks, it deals 3 damage to target creature or planeswalker an opponent controls.
 * Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes an artifact creature until end of turn.)
 *
 * "Enters **or** attacks" is two trigger conditions on one printed line, and the SDK models a
 * trigger as a single event pattern, so it is authored as two triggered abilities with the same
 * effect and the same target requirement. Each fires independently — a Vehicle that enters and
 * later attacks shoots twice.
 *
 * Crew is the engine's own keyword ability; the Vehicle type on the type line plus
 * [KeywordAbility.crew] is the whole implementation.
 */
val SkysovereignConsulFlagship = card("Skysovereign, Consul Flagship") {
    manaCost = "{5}"
    typeLine = "Legendary Artifact — Vehicle"
    oracleText = "Flying\n" +
        "Whenever Skysovereign enters or attacks, it deals 3 damage to target creature or planeswalker an opponent controls.\n" +
        "Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 6
    toughness = 5

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls()))
        )
        effect = Effects.DealDamage(3, t)
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls()))
        )
        effect = Effects.DealDamage(3, t)
    }

    keywordAbility(KeywordAbility.crew(3))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "234"
        artist = "Jung Park"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1df70ef3-8919-43ac-9317-23548437a181.jpg?1783937149"
    }
}
