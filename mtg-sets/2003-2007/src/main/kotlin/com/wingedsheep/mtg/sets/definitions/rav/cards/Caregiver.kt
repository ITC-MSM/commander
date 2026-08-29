package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Caregiver
 * {W}
 * Creature — Human Cleric
 * 1/1
 * {W}, Sacrifice a creature: Prevent the next 1 damage that would be dealt to any target this turn.
 *
 * The sacrifice is an unrestricted "a creature" — Caregiver itself is a legal choice, so the cost
 * takes [GameObjectFilter.Creature] with no `excludeSelf`.
 */
val Caregiver = card("Caregiver") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "{W}, Sacrifice a creature: Prevent the next 1 damage that would be dealt to any target this turn."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{W}"),
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        val t = target("any target", Targets.Any)
        effect = Effects.PreventNextDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "William Simpson"
        flavorText = "\"The guilds each believe its way is right, but their ways only bring blood to Ravnica's streets and tears to Ravnica's families.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c33d3fe-cdd3-4829-8f10-6611f063983b.jpg"
    }
}
