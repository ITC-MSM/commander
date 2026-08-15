package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Turtle Lair
 * Land
 *
 * {T}: Add {C}.
 * {T}: Add one mana of any color. Spend this mana only to cast a Ninja or Turtle spell.
 * {3}, {T}: Target Ninja or Turtle can't be blocked this turn.
 */
val TurtleLair = card("Turtle Lair") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n{T}: Add one mana of any color. Spend this mana only to cast a Ninja or Turtle spell.\n{3}, {T}: Target Ninja or Turtle can't be blocked this turn."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            1,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Ninja", "Turtle"))
        )
        manaAbility = true
    }

    activatedAbility {
        val creature = target(
            "target Ninja or Turtle",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature.withAnyOfSubtypes(listOf(Subtype("Ninja"), Subtype("Turtle")))
                )
            )
        )
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Marina Ortega Lorente"
        flavorText = "Home is where your pizza delivers."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c50c4580-979b-4863-86b9-16e258198972.jpg?1771424738"
    }
}
