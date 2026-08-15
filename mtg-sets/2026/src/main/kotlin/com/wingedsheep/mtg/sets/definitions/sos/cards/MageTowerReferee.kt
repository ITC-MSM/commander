package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mage Tower Referee — Secrets of Strixhaven #249
 * {2} · Artifact Creature — Construct · 2/1
 *
 * Whenever you cast a multicolored spell, put a +1/+1 counter on this creature.
 */
val MageTowerReferee = card("Mage Tower Referee") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Construct"
    power = 2
    toughness = 1
    oracleText = "Whenever you cast a multicolored spell, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Multicolored)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Nino Vecia"
        flavorText = "Incapable of choosing a favorite and invulnerable to the influence of magic, " +
            "it is an ideal and obnoxiously accurate referee."
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1ceb704a-97a8-49f9-b799-30f001404144.jpg?1775938737"
    }
}
