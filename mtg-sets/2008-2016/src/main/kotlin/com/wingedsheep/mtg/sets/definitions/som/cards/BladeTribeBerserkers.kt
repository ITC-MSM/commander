package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blade-Tribe Berserkers
 * {3}{R}
 * Creature — Human Berserker
 * 3/3
 *
 * Metalcraft — When this creature enters, if you control three or more artifacts, this creature gets +3/+3 and gains haste until end of turn.
 *
 * "Metalcraft" is an ability word (CR 207.2c) with no rules meaning of its own — there is no
 * `Keyword.METALCRAFT`, and nothing but the oracle line records it. What the line actually is:
 * an ETB trigger whose printed "if …" clause is an intervening-if condition
 * ([Conditions.YouControlAtLeast]), checked both when the trigger would go on the stack and again
 * as it resolves (CR 603.4).
 */
val BladeTribeBerserkers = card("Blade-Tribe Berserkers") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Berserker"
    power = 3
    toughness = 3
    oracleText = "Metalcraft — When this creature enters, if you control three or more artifacts, this creature gets +3/+3 and gains haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 3, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
        )
        description = "Metalcraft — When this creature enters, if you control three or more artifacts, " +
            "this creature gets +3/+3 and gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/acd124bb-1ed1-469c-8527-d7261ea720b9.jpg?1783941726"
    }
}
