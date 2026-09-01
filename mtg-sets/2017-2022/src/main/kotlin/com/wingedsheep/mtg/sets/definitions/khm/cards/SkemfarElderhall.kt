package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Skemfar Elderhall
 * Land
 * This land enters tapped.
 * {T}: Add {G}.
 * {2}{B}{B}{G}, {T}, Sacrifice this land: Up to one target creature you don't control gets -2/-2 until end of turn. Create two 1/1 green Elf Warrior creature tokens. Activate only as a sorcery.
 *
 * The Golgari realm land. The target is "up to one", so the ability can be activated purely for the
 * two Elf Warriors when there is nothing worth shrinking — `optional = true` on the requirement is
 * what makes an empty board a legal activation.
 *
 * "you don't control" is spelled as `Not(ControlledByYou)` rather than `ControlledByOpponent`. The two
 * coincide in a duel and diverge in Two-Headed Giant, where a teammate's creature is one you don't
 * control but is not an opponent's.
 */
val SkemfarElderhall = card("Skemfar Elderhall") {
    manaCost = ""
    colorIdentity = "BG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {G}.\n" +
        "{2}{B}{B}{G}, {T}, Sacrifice this land: Up to one target creature you don't control gets -2/-2 until end of turn. Create two 1/1 green Elf Warrior creature tokens. Activate only as a sorcery."

    replacementEffect(EntersTapped())

    // {T}: Add {G}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN, 1)
        manaAbility = true
    }

    // {2}{B}{B}{G}, {T}, Sacrifice this land: Up to one target creature you don't control gets
    // -2/-2 until end of turn. Create two 1/1 green Elf Warrior creature tokens.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}{B}{G}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        val victim = target(
            "target",
            TargetObject(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.Creature.copy(
                        controllerPredicate = ControllerPredicate.Not(ControllerPredicate.ControlledByYou)
                    )
                )
            )
        )
        effect = Effects.Composite(
            Effects.ModifyStats(-2, -2, victim),
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Elf", "Warrior"),
                count = 2
            )
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "268"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82c2a0f7-0f53-4627-8be8-227fde331a69.jpg"
    }
}
