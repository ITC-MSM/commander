package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Redcap Gutter-Dweller
 * {2}{R}{R}
 * Creature — Goblin Warrior
 * 3/3
 *
 * Menace
 * When this creature enters, create two 1/1 black Rat creature tokens with
 * "This token can't block."
 * At the beginning of your upkeep, you may sacrifice another creature. If you do,
 * put a +1/+1 counter on this creature and exile the top card of your library.
 * You may play that card this turn.
 *
 * Implementation notes:
 * - The upkeep trigger is an [OptionalCostEffect]: the sacrifice is the optional
 *   cost (`excludeSource` enforces "another"), and paying it runs the counter +
 *   impulse rewards. Per the 2024-11-08 ruling, if Redcap leaves the battlefield
 *   before the trigger resolves, the sacrifice and impulse still happen — the
 *   [Effects.AddCounters] on the departed source simply no-ops while the rest of
 *   the composite continues.
 */
val RedcapGutterDweller = card("Redcap Gutter-Dweller") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 3
    toughness = 3
    oracleText = "Menace\n" +
        "When this creature enters, create two 1/1 black Rat creature tokens with \"This token can't block.\"\n" +
        "At the beginning of your upkeep, you may sacrifice another creature. If you do, " +
        "put a +1/+1 counter on this creature and exile the top card of your library. " +
        "You may play that card this turn."

    keywords(Keyword.MENACE)

    // When this creature enters, create two 1/1 black Rat tokens with "This token can't block."
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = woeRatToken(count = DynamicAmount.Fixed(2))
    }

    // At the beginning of your upkeep, you may sacrifice another creature. If you do,
    // put a +1/+1 counter on this creature and impulse the top card of your library.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = OptionalCostEffect(
            cost = SacrificeEffect(
                filter = GameObjectFilter.Creature,
                count = 1,
                excludeSource = true,
            ),
            ifPaid = Effects.Composite(listOf(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                Patterns.Exile.impulse(count = 1, storeAs = "redcapImpulseExiled"),
            )),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "146"
        artist = "Alexey Kruglov"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96bcd5f0-da79-47ab-83cf-976198b458d1.jpg?1783915091"
        ruling(
            "2024-11-08",
            "If Redcap Gutter-Dweller leaves the battlefield after its last ability has triggered, " +
                "you can still sacrifice a creature and exile the top card of your library, even though " +
                "you won't put a +1/+1 counter on Redcap Gutter-Dweller."
        )
        ruling(
            "2024-11-08",
            "You pay all costs and follow all normal timing rules for cards played with the permission " +
                "granted by Redcap Gutter-Dweller's last ability. For example, if the exiled card is a " +
                "land card, you may play it only during your main phase while the stack is empty."
        )
    }
}
