package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Meria, Scholar of Antiquity
 * {1}{R}{G}
 * Legendary Creature — Elf Artificer
 * 3/3
 * Tap an untapped nontoken artifact you control: Add {G}.
 * Tap two untapped nontoken artifacts you control: Exile the top card of your library. You may play it this turn.
 */
val MeriaScholarOfAntiquity = card("Meria, Scholar of Antiquity") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Elf Artificer"
    oracleText = "Tap an untapped nontoken artifact you control: Add {G}.\nTap two untapped nontoken artifacts you control: Exile the top card of your library. You may play it this turn."
    power = 3
    toughness = 3

    // Both costs tap *nontoken* artifacts; `TapPermanents` already restricts the
    // candidates to untapped permanents their controller controls.
    activatedAbility {
        cost = Costs.TapPermanents(1, GameObjectFilter.Artifact.nontoken())
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.TapPermanents(2, GameObjectFilter.Artifact.nontoken())
        effect = Patterns.Exile.impulse(count = 1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "206"
        artist = "Aurore Folny"
        flavorText = "\"Why fear artifice? Is it not also of this world?\""
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3eedd979-783d-4721-92de-965b77c20576.jpg?1783921281"
    }
}
