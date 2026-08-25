package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Mox Tantalite — Modern Horizons #226
 * (no mana cost) · Artifact
 *
 * Suspend 3—{0}
 * {T}: Add one mana of any color.
 *
 * The Sol Talisman shape two years earlier: printed with **no mana cost**, which CR 202.1b/118.6
 * make unpayable, so suspend is the only route onto the battlefield. `manaCost = ""` is what
 * `CardBuilder.build()` reads to set `hasNoManaCost` — `"{0}"` would parse to a payable zero cost
 * and leave it castable for free, a different card. Note that suspending it still costs {0}, which
 * is a real (if trivial) payment, and the three-turn wait is the whole price.
 *
 * Genuinely colorless (CR 202.2), so no `colorIndicator`: unlike Ancestral Vision or Crashing
 * Footfalls there is no printed color the missing mana cost is hiding.
 *
 * Suspend is card-type agnostic (CR 702.62a); the display-only `Keyword.SUSPEND` is derived from
 * the parameterized [KeywordAbility.Suspend] by the builder.
 *
 * "Add one mana of any color" is [Effects.AddManaOfChoice]'s default shape — all five colors, one
 * mana — and `manaAbility = true` derives `TimingRule.ManaAbility`.
 */
val MoxTantalite = card("Mox Tantalite") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Suspend 3—{0} (Rather than cast this card from your hand, pay {0} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)\n" +
        "{T}: Add one mana of any color."

    keywordAbility(KeywordAbility.suspend("{0}", 3))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "226"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcd05b01-dc73-4dd2-970a-32ec6e153c86.jpg?1783933074"
    }
}
