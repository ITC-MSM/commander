package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Argothian Sprite
 * {1}{G}
 * Creature — Faerie
 * 2/2
 * This creature can't be blocked by artifact creatures.
 * {7}: Put two +1/+1 counters on this creature.
 *
 * The blocking restriction is the same [CantBeBlockedBy] over [GameObjectFilter.ArtifactCreature]
 * that Argothian Pixies (ATQ) prints.
 */
val ArgothianSprite = card("Argothian Sprite") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Faerie"
    power = 2
    toughness = 2
    oracleText = "This creature can't be blocked by artifact creatures.\n" +
        "{7}: Put two +1/+1 counters on this creature."

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.ArtifactCreature)
    }

    activatedAbility {
        cost = Costs.Mana("{7}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Rudy Siswanto"
        flavorText = "\"Make the exhaust vents smaller next time!\"\n—Urza, notes to his engineers"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5dbc383-7e08-4701-8d4a-6b99b74fe358.jpg?1783920053"
    }
}
