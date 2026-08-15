package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.sneak
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Leonardo's Technique
 * {3}{W}
 * Sorcery
 *
 * Sneak {1}{W}
 * Return one or two target creature cards each with mana value 3 or less from
 * your graveyard to the battlefield.
 */
val LeonardosTechnique = card("Leonardo's Technique") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Sneak {1}{W} (You may cast this spell for {1}{W} if you also return an unblocked attacker you control to hand during the declare blockers step.)\nReturn one or two target creature cards each with mana value 3 or less from your graveyard to the battlefield."

    sneak("{1}{W}")

    spell {
        // "one or two target" = count 2, minCount 1; each must be a creature card you own
        // in your graveyard with mana value 3 or less.
        target = TargetObject(
            count = 2,
            minCount = 1,
            filter = TargetFilter(
                GameObjectFilter.Creature.ownedByYou().manaValueAtMost(3),
                zone = Zone.GRAVEYARD
            )
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.Move(EffectTarget.ContextTarget(0), Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Andreas Zafiratos"
        flavorText = "\"That's right. Eyes on me.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/405e4057-e26c-4882-89a9-706868548c37.jpg?1769005546"
    }
}
