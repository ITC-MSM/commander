package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Castle Vantress
 *
 * Land
 * This land enters tapped unless you control an Island.
 * {T}: Add {U}.
 * {2}{U}{U}, {T}: Scry 2.
 *
 * The Castle cycle's entry clause is [EntersTapped] with an `unlessCondition` of [Exists] over
 * Islands you control — a subtype check, so a dual or a nonbasic Island satisfies it just as well
 * as the basic. The type line is bare `Land` (no Island subtype of its own), so the {U} tap is a
 * written mana ability rather than an intrinsic one, and the payoff needs nothing new: it is
 * [Effects.Scry] behind a mana-plus-tap [Costs.Composite].
 */
val CastleVantress = card("Castle Vantress") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control an Island.\n" +
        "{T}: Add {U}.\n" +
        "{2}{U}{U}, {T}: Scry 2."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Land.withSubtype("Island"),
            )
        )
    )

    // {T}: Add {U}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {2}{U}{U}, {T}: Scry 2.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}{U}"), Costs.Tap)
        effect = Effects.Scry(2)
        description = "{2}{U}{U}, {T}: Scry 2."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "242"
        artist = "John Avon"
        flavorText = "Without Vantress's knowledge, the realm would lose itself in darkness."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a8b9d37-e89c-44ad-bd1b-51cb06ec3e0b.jpg?1783932578"
    }
}
