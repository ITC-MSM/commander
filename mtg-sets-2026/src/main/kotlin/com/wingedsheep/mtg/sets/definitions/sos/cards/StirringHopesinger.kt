package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stirring Hopesinger
 * {2}{W}
 * Creature — Bird Bard
 * 1/3
 * Flying, lifelink
 * Repartee — Whenever you cast an instant or sorcery spell that targets a creature,
 * put a +1/+1 counter on each creature you control.
 *
 * "Repartee" is an ability word (flavor only, no keyword). The trigger fires on casting an
 * instant/sorcery that targets a creature; the effect iterates every creature you control —
 * including Stirring Hopesinger itself (no excludeSelf) — putting a +1/+1 counter on each.
 */
val StirringHopesinger = card("Stirring Hopesinger") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Bard"
    oracleText = "Flying, lifelink\nRepartee — Whenever you cast an instant or sorcery spell that targets a creature, put a +1/+1 counter on each creature you control."
    power = 1
    toughness = 3
    keywords(Keyword.FLYING, Keyword.LIFELINK)
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.InstantOrSorcery.targetsMatching(GameObjectFilter.Creature)
        )
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Cristi Balanescu"
        flavorText = "Though not all her students have wings, she's determined to help them reach new heights."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21375667-b318-47f8-a482-9c8c2b5b14c0.jpg?1775937157"
    }
}
