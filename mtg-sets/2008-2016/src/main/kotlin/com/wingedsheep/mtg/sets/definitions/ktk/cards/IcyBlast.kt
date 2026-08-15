package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Icy Blast
 * {X}{U}
 * Instant
 * Tap X target creatures.
 * Ferocious — If you control a creature with power 4 or greater, those creatures don't untap
 * during their controllers' next untap steps.
 */
val IcyBlast = card("Icy Blast") {
    manaCost = "{X}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Tap X target creatures.\nFerocious — If you control a creature with power 4 or greater, those creatures don't untap during their controllers' next untap steps."

    spell {
        // "Tap X target creatures" — the chosen X clamps the number of targets via
        // dynamicMaxCount (Builder's Bane / Distorting Wake pattern), so no magic count.
        target = TargetCreature(optional = true, dynamicMaxCount = DynamicAmount.XValue)
        effect = Effects.TapEachTarget()
            .then(ConditionalEffect(
                condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature.powerAtLeast(4)),
                effect = ForEachTargetEffect(listOf(
                    GrantKeywordEffect(
                        AbilityFlag.DOESNT_UNTAP.name,
                        EffectTarget.ContextTarget(0),
                        Duration.UntilAfterAffectedControllersNextUntap
                    )
                ))
            ))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Eric Deschamps"
        flavorText = "\"Do not think the sand or the sun will hold back the breath of winter.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b098f029-6b5d-49e4-9a81-f497ebbdb5ce.jpg?1562792016"
    }
}
