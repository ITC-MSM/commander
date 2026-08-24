package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Oxidda Daredevil — Scars of Mirrodin #100
 * {1}{R} · Creature — Goblin Artificer · 2 / 1
 *
 * Sacrifice an artifact: This creature gains haste until end of turn.
 *
 * A free activated ability whose whole cost is the sacrifice — no mana, no tap — so it can be
 * activated as often as there are artifacts to feed it. `Costs.Sacrifice` (not `SacrificeAnother`):
 * the Daredevil is not itself an artifact, so there is nothing to exclude.
 */
val OxiddaDaredevil = card("Oxidda Daredevil") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Artificer"
    power = 2
    toughness = 1
    oracleText = "Sacrifice an artifact: This creature gains haste until end of turn."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Artifact)
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Pete Venters"
        flavorText = "His goggles spattered with grime and his mouth full of bugs, he tossed the engines another priceless relic."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b0bde7b-dc2d-45d2-b124-69b4b51ef3d9.jpg?1783941723"
    }
}
