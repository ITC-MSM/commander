package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Horror of Horrors
 * {3}{B}{B}
 * Enchantment
 *
 * Sacrifice a Swamp: Regenerate target black creature. (The next time that creature would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)
 */
val HorrorOfHorrors = card("Horror of Horrors") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Sacrifice a Swamp: Regenerate target black creature. (The next time that creature would be " +
        "destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Land.withSubtype(Subtype.SWAMP))
        val creature = target(
            "target black creature",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withColor(Color.BLACK))),
        )
        effect = RegenerateEffect(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "106"
        artist = "Mark Tedin"
        flavorText = "\"And a horror of outer darkness after,/ And dust returneth to dust again.\"\n" +
            "—Adam Lindsay Gordon, The Swimmer"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9f68dc2-c048-41ec-b237-c36fdd99c27d.jpg?1783948064"
    }
}
