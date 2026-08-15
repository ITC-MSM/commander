package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prompto Argentum
 * {1}{R}
 * Legendary Creature — Human Scout
 * 2/2
 *
 * Haste
 * Selfie Shot — Whenever you cast a noncreature spell, if at least four mana was spent to cast it,
 * create a Treasure token.
 *
 * "Selfie Shot" is an ability word (flavor only — no rules meaning). "If at least four mana was
 * spent to cast it" is an intervening-if on the noncreature-cast trigger, modeled by
 * [Conditions.TriggeringSpellManaSpentAtLeast] reading the triggering spell's recorded total mana
 * paid — so an {X} spell that paid four or more qualifies, while a four-mana-value spell cast for
 * less (cost reduction) does not. Shares its trigger shape with Sahagin.
 */
val PromptoArgentum = card("Prompto Argentum") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Scout"
    power = 2
    toughness = 2
    oracleText = "Haste\nSelfie Shot — Whenever you cast a noncreature spell, if at least four mana " +
        "was spent to cast it, create a Treasure token."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        triggerCondition = Conditions.TriggeringSpellManaSpentAtLeast(4)
        effect = Effects.CreateTreasure()
        description = "Selfie Shot — Whenever you cast a noncreature spell, if at least four mana " +
            "was spent to cast it, create a Treasure token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "148"
        artist = "Billy Christian"
        flavorText = "\"Look out world, here we come!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c617bcd-05f8-40c2-bb38-489bc863ce6b.jpg?1748706313"
    }
}
