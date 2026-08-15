package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mild-Mannered Librarian (J22 #41)
 * {G}  Creature — Human  1/1
 *
 * {3}{G}: This creature becomes a Werewolf. Put two +1/+1 counters on it and you draw a card.
 * Activate only once.
 *
 * "Becomes a Werewolf" replaces its creature subtypes (Human → Werewolf) permanently, so it is a
 * [Effects.SetCreatureSubtypes] with `Duration.Permanent`. "Activate only once" is
 * [ActivationRestriction.Once] (once for the lifetime of this permanent, CR-faithful to a
 * per-object activation limit).
 */
val MildManneredLibrarian = card("Mild-Mannered Librarian") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human"
    power = 1
    toughness = 1
    oracleText = "{3}{G}: This creature becomes a Werewolf. Put two +1/+1 counters on it and you " +
        "draw a card. Activate only once."

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.Composite(
            listOf(
                Effects.SetCreatureSubtypes(setOf("Werewolf"), EffectTarget.Self, Duration.Permanent),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
                Effects.DrawCards(1)
            )
        )
        restrictions = listOf(ActivationRestriction.Once)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Justyna Dura"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3eee6f29-ef06-47f6-99af-fb0ff88f09d0.jpg?1782699338"
    }
}
