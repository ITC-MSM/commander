package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fyndhorn Bow
 * {2}
 * Artifact
 *
 * {3}, {T}: Target creature gains first strike until end of turn.
 *
 * The Ice Age keyword-granting equipment shape: mana + tap, then [Effects.GrantKeyword] at its
 * default end-of-turn duration.
 */
val FyndhornBow = card("Fyndhorn Bow") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{3}, {T}: Target creature gains first strike until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "318"
        artist = "Rob Alexander"
        flavorText = "\"With a bow like this, the hunting is always good.\"\n—Taaveti of Kelsinko, Elvish Hunter"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65dd0a41-cc51-4728-b597-fdb2510accd8.jpg"
    }
}
