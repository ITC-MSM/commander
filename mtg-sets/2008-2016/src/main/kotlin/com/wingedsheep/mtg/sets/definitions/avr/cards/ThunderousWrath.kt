package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thunderous Wrath
 * {4}{R}{R}
 * Instant
 *
 * Thunderous Wrath deals 5 damage to any target.
 * Miracle {R} (You may cast this card for its miracle cost when you draw it if it's the first card you drew this turn.)
 *
 * A plain [Effects.DealDamage] at an [Targets.Any] target — no `damageSource` override, so the
 * engine attributes the damage to the resolving spell itself. Miracle is the standard
 * [KeywordAbility.miracle] first-draw-of-turn alternative cost.
 */
val ThunderousWrath = card("Thunderous Wrath") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Thunderous Wrath deals 5 damage to any target.\n" +
        "Miracle {R} (You may cast this card for its miracle cost when you draw it if it's the first " +
        "card you drew this turn.)"

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(5, t)
    }

    keywordAbility(KeywordAbility.miracle("{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/daa39826-7f89-41cb-a7fe-7f7be817d5cd.jpg?1783940675"
    }
}
