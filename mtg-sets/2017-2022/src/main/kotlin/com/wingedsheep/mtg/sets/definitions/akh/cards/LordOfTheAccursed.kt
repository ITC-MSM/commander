package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lord of the Accursed
 * {2}{B}
 * Creature — Zombie
 * 2/3
 * Other Zombies you control get +1/+1.
 * {1}{B}, {T}: All Zombies gain menace until end of turn.
 *
 * The lord clause is a static [ModifyStats] over Zombies you control with
 * `excludeSelf = true` ("other"). The activated ability is deliberately *not*
 * controller-scoped — "All Zombies" reaches every Zombie on the battlefield,
 * including opponents', so its [GroupFilter] carries no `youControl()`.
 */
val LordOfTheAccursed = card("Lord of the Accursed") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 3
    oracleText = "Other Zombies you control get +1/+1.\n{1}{B}, {T}: All Zombies gain menace until end of turn."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE)),
            Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Grzegorz Rutkowski"
        flavorText = "The Curse of Wandering leaves only hatred."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3941562d-3e39-4746-9a18-d3aa6d3468b0.jpg?1783936503"
    }
}
