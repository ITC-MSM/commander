package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Maze of Shadows
 * Land
 *
 * {T}: Add {C}.
 * {T}: Untap target attacking creature with shadow. Prevent all combat damage
 * that would be dealt to and dealt by that creature this turn.
 */
val MazeOfShadows = card("Maze of Shadows") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{T}: Untap target attacking creature with shadow. Prevent all combat damage " +
        "that would be dealt to and dealt by that creature this turn."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        val shadowAttacker = target(
            "target attacking creature with shadow",
            TargetCreature(filter = TargetFilter.AttackingCreature.withKeyword(Keyword.SHADOW)),
        )
        effect = Effects.Untap(shadowAttacker)
            .then(Effects.PreventCombatDamageToAndBy(shadowAttacker))
        description = "{T}: Untap target attacking creature with shadow. Prevent all combat damage " +
            "that would be dealt to and dealt by that creature this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "319"
        artist = "D. Alexander Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba69c3d3-6fb5-478d-93ba-341dd3ace97d.jpg?1562056379"
    }
}
