package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Olivia Voldaren
 * {2}{B}{R}
 * Legendary Creature — Vampire
 * 3/3
 * Flying
 * {1}{R}: Olivia Voldaren deals 1 damage to another target creature. That creature becomes a
 *   Vampire in addition to its other types. Put a +1/+1 counter on Olivia Voldaren.
 * {3}{B}{B}: Gain control of target Vampire for as long as you control Olivia Voldaren.
 *
 * "Becomes a Vampire in addition to its other types" is the additive [Effects.AddSubtype] with an
 * indefinite [Duration.Permanent]. The control grant ends the moment Olivia's controller changes,
 * modeled by [Duration.WhileYouControlSource].
 */
val OliviaVoldaren = card("Olivia Voldaren") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Vampire"
    oracleText = "Flying\n" +
        "{1}{R}: Olivia Voldaren deals 1 damage to another target creature. That creature becomes a Vampire in addition to its other types. Put a +1/+1 counter on Olivia Voldaren.\n" +
        "{3}{B}{B}: Gain control of target Vampire for as long as you control Olivia Voldaren."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        val creature = target("another target creature", TargetCreature(filter = TargetFilter.OtherCreature))
        effect = Effects.Composite(
            listOf(
                Effects.DealDamage(1, creature),
                Effects.AddSubtype("Vampire", creature, Duration.Permanent),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
        )
        description = "{1}{R}: Olivia Voldaren deals 1 damage to another target creature. That creature becomes a Vampire in addition to its other types. Put a +1/+1 counter on Olivia Voldaren."
    }

    activatedAbility {
        cost = Costs.Mana("{3}{B}{B}")
        val vampire = target(
            "target Vampire",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Vampire")))
        )
        effect = Effects.GainControl(vampire, Duration.WhileYouControlSource("Olivia Voldaren"))
        description = "{3}{B}{B}: Gain control of target Vampire for as long as you control Olivia Voldaren."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "215"
        artist = "Eric Deschamps"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed750692-ba6a-4a89-ad6d-92fda7edc2cb.jpg?1782714696"
    }
}
