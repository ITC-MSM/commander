package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Facevaulter
 * {B}
 * Creature — Goblin Warrior
 * 1/1
 * {B}, Sacrifice a Goblin: This creature gets +2/+2 until end of turn.
 *
 * "A Goblin" includes Facevaulter itself (it is a Goblin Warrior), so [Costs.Sacrifice] rather
 * than `SacrificeAnother`.
 */
val Facevaulter = card("Facevaulter") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 1
    oracleText = "{B}, Sacrifice a Goblin: This creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN))
        )
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "{B}, Sacrifice a Goblin: This creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Wayne Reynolds"
        flavorText = "Boggarts get so excited when they find something new to smash that they really don't notice who gets underfoot."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1fc973d3-39cf-4d0c-a3e9-70af2be3cd68.jpg?1783942890"
    }
}
