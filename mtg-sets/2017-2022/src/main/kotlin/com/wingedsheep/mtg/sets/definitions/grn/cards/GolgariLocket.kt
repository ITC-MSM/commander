package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Golgari Locket
 * {3}
 * Artifact
 * {T}: Add {B} or {G}.
 * {B/G}{B/G}{B/G}{B/G}, {T}, Sacrifice this artifact: Draw two cards.
 */
val GolgariLocket = card("Golgari Locket") {
    manaCost = "{3}"
    colorIdentity = "BG"
    typeLine = "Artifact"
    oracleText = "{T}: Add {B} or {G}.\n" +
        "{B/G}{B/G}{B/G}{B/G}, {T}, Sacrifice this artifact: Draw two cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B/G}{B/G}{B/G}{B/G}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "237"
        artist = "Milivoj Ćeran"
        flavorText = "\"Wear it at all times. It will guide our reanimators to your corpse.\"\n—Mazirek, kraul death priest"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c2e7843-9a87-46cb-be06-6db58649db85.jpg?1783934106"
    }
}
