package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MustAttack
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sabertooth Alley Cat
 * {1}{R}{R}
 * Creature — Cat
 * 2/1
 *
 * This creature attacks each combat if able.
 * {1}{R}: Creatures without defender can't block this creature this turn.
 *
 * The activated ability is written from the *attacker's* side. "Creatures without defender can't
 * block this creature" and "this creature can't be blocked except by creatures with defender" name
 * the same set of legal blockers — every creature either has defender or it doesn't — so this is
 * [Effects.GrantCantBeBlockedExceptBy] with a `withKeyword(DEFENDER)` blocker filter rather than a
 * group-wide [com.wingedsheep.sdk.scripting.effects.CantBlockGroupEffect], which would also stop
 * those creatures blocking *other* attackers. It rides the same projected
 * `cantBeBlockedExceptByFilters` channel the printed `CantBeBlockedExceptBy` static uses, and the
 * filter is re-read at declare-blockers, so a creature that gains or loses defender after
 * activation is judged on its state at that moment.
 */
val SabertoothAlleyCat = card("Sabertooth Alley Cat") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 1
    oracleText = "This creature attacks each combat if able.\n" +
        "{1}{R}: Creatures without defender can't block this creature this turn."

    staticAbility {
        ability = MustAttack()
    }

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.GrantCantBeBlockedExceptBy(
            EffectTarget.Self,
            GameObjectFilter.Creature.withKeyword(Keyword.DEFENDER)
        )
        description = "Creatures without defender can't block this creature this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Carl Critchlow"
        flavorText = "It has eight lives' worth of hunger to satisfy."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9ec817c-c414-4a56-b455-748c6e5cac0d.jpg?1783943647"
    }
}
