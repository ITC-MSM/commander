package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Steward of Valeron
 * {G}{W}
 * Creature — Human Druid Knight
 * 2 / 2
 * Vigilance
 * {T}: Add {G}.
 *
 * A mana creature that keeps guard: `keywords(`[Keyword.VIGILANCE]`)` (from which the builder
 * derives the simple keyword ability), and a single [Effects.AddMana]`(`[Color.GREEN]`)` on
 * [Costs.Tap] flagged `manaAbility` with [TimingRule.ManaAbility] so it never uses the stack —
 * the same one-colour shape as [DruidOfTheAnima]'s three abilities.
 */
val StewardOfValeron = card("Steward of Valeron") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Druid Knight"
    power = 2
    toughness = 2
    oracleText = "Vigilance\n" +
        "{T}: Add {G}."

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Greg Staples"
        flavorText = "Knight-stewards guard the Sun-Dappled Court, a grove of immense, sculptured olive trees that represent Valeron's twelve noble families."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92e21ff9-0030-4e53-8ddf-86d8e78e347f.jpg"
    }
}
