package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetSpell
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Spellstutter Sprite
 * {1}{U}
 * Creature — Faerie Wizard
 * 1/1
 * Flash
 * Flying
 * When this creature enters, counter target spell with mana value X or less, where X is the number
 * of Faeries you control.
 *
 * Modelling notes:
 * - **The cap is a targeting restriction, not a resolution check.** It rides on the target filter as
 *   `manaValueAtMostDynamic`, so the count is read when the trigger picks a target *and* again when
 *   it resolves — exactly the 2007-10-01 ruling: if the Faerie count has dropped enough in between,
 *   the ability is countered on resolution for having no legal target and the spell resolves.
 * - **"The number of Faeries you control" is a bare tribal noun**, so it counts Faerie *permanents*,
 *   not just Faerie creatures. Lorwyn prints Kindred noncreature Faeries, and the Sprite itself is
 *   already on the battlefield when its own enters trigger resolves, so it always counts itself.
 */
val SpellstutterSprite = card("Spellstutter Sprite") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 1
    toughness = 1
    oracleText = "Flash\nFlying\nWhen this creature enters, counter target spell with mana value X " +
        "or less, where X is the number of Faeries you control."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetSpell(
            filter = TargetFilter.SpellOnStack.manaValueAtMostDynamic(
                DynamicAmount.AggregateBattlefield(
                    player = Player.You,
                    filter = GameObjectFilter.Permanent.withSubtype(Subtype.FAERIE),
                )
            )
        )
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e5ba4a9-a282-4d4b-b25a-179e05e458f4.jpg?1783942897"
        ruling(
            "2007-10-01",
            "The value of X needs to be determined both when the ability triggers (so you can " +
                "choose a target) and again when the ability resolves (to check if that target is " +
                "still legal). If the number of Faeries you control has decreased enough in that " +
                "time to make the target illegal, Spellstutter Sprite's ability won't resolve (and " +
                "the targeted spell will resolve as normal)."
        )
    }
}
