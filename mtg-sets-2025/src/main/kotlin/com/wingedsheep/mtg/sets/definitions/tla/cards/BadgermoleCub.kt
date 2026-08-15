package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Badgermole Cub — {1}{G}
 * Creature — Badger Mole
 * 2/2
 * When this creature enters, earthbend 1.
 * Whenever you tap a creature for mana, add an additional {G}.
 */
val BadgermoleCub = card("Badgermole Cub") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Badger Mole"
    oracleText = "When this creature enters, earthbend 1. (Target land you control becomes a 0/0 creature with haste that's still a land. Put a +1/+1 counter on it. When it dies or is exiled, return it to the battlefield tapped.)\nWhenever you tap a creature for mana, add an additional {G}."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Earthbend(1, land)
        description = "When this creature enters, earthbend 1."
    }

    staticAbility {
        ability = AdditionalManaOnSourceTap(
            sourceFilter = GameObjectFilter.Creature.youControl(),
            color = Color.GREEN
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "167"
        artist = "Nathaniel Himawan"
        flavorText = "Every mountain starts as a molehill."
        imageUri = "https://cards.scryfall.io/normal/front/3/4/340c5799-4964-44dd-8c48-8f3f3aba5211.jpg?1764121140"
    }
}
