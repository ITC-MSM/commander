package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Underfoot Underdogs — Tarkir: Dragonstorm #129
 * {2}{R} · Creature — Goblin Warrior · 1/2
 *
 * When this creature enters, create a 1/1 red Goblin creature token.
 * {1}, {T}: Target creature you control with power 2 or less can't be blocked this turn.
 *
 * ETB token via [CreateTokenEffect]; the activated ability reuses the Crafty Pathmage pattern —
 * [GrantKeywordEffect] of [AbilityFlag.CANT_BE_BLOCKED] (default end-of-turn duration) on a target
 * creature you control restricted to power 2 or less.
 */
val UnderfootUnderdogs = card("Underfoot Underdogs") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 2
    oracleText = "When this creature enters, create a 1/1 red Goblin creature token.\n" +
        "{1}, {T}: Target creature you control with power 2 or less can't be blocked this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = 1,
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            imageUri = "https://cards.scryfall.io/normal/front/e/2/e265ca24-96c0-4654-a8f3-bbffe288970a.jpg?1742506636",
        )
        description = "When this creature enters, create a 1/1 red Goblin creature token."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val t = target(
            "target",
            TargetCreature(
                filter = TargetFilter(GameObjectFilter.Creature.powerAtMost(2).youControl())
            )
        )
        effect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, t)
        description = "{1}, {T}: Target creature you control with power 2 or less can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Brent Hollowell"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/049acc79-1d68-410f-a081-88a7d40e823a.jpg?1743204484"
    }
}
