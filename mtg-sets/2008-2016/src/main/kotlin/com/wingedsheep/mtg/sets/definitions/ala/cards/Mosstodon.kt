package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mosstodon
 * {4}{G}
 * Creature — Plant Elephant
 * 5 / 3
 * {1}: Target creature with power 5 or greater gains trample until end of turn.
 *
 * A bare [Costs.Mana] activation with no tap, so it can be used repeatedly. "with power 5 or
 * greater" is a predicate on the target itself — `GameObjectFilter.Creature.powerAtLeast(5)`
 * wrapped in a [TargetFilter] — and the grant is [Effects.GrantKeyword], whose default duration
 * is already until end of turn.
 */
val Mosstodon = card("Mosstodon") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Elephant"
    power = 5
    toughness = 3
    oracleText = "{1}: Target creature with power 5 or greater gains trample until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}")
        val t = target("target", TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(5))))
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Paolo Parente"
        flavorText = "Whether gargantuans are the manifested will of Progenitus or simply the result of overabundant resources is moot when a herd of them is thundering at you."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05afd921-ef3b-40fe-a1a8-582ee94ed3f0.jpg"
    }
}
