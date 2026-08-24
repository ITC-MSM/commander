package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Experimental Synthesizer — Kamigawa: Neon Dynasty #138 (canonical printing)
 * {R} · Artifact
 *
 * When this artifact enters or leaves the battlefield, exile the top card of your library. Until
 * end of turn, you may play that card.
 * {2}{R}, Sacrifice this artifact: Create a 2/2 white Samurai creature token with vigilance.
 * Activate only as a sorcery.
 *
 * A one-mana artifact that impulse-draws twice — once on the way in, once on the way out — which
 * is what makes it a sacrifice payoff and an artifact-recursion payoff at the same time. Its own
 * activated ability sacrifices it, so activating it collects the leave trigger too.
 *
 * "Enters **or** leaves" is written as two triggered abilities over [Triggers.EntersBattlefield]
 * and [Triggers.LeavesBattlefield]. The two events can never happen at once, so one ability with a
 * two-event pattern and two abilities are the same card in play; two abilities is the shape the
 * corpus already uses for this wording (Cryogen Relic).
 *
 * Both halves are [Patterns.Exile] `.impulse(1)` — exile the top card and grant permission to play
 * it until end of turn, at its normal cost.
 */
val ExperimentalSynthesizer = card("Experimental Synthesizer") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters or leaves the battlefield, exile the top card of your " +
        "library. Until end of turn, you may play that card.\n" +
        "{2}{R}, Sacrifice this artifact: Create a 2/2 white Samurai creature token with " +
        "vigilance. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Exile.impulse(1)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Patterns.Exile.impulse(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}"), Costs.SacrificeSelf)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Samurai"),
            keywords = setOf(Keyword.VIGILANCE),
            name = "Samurai",
            imageUri = "https://cards.scryfall.io/normal/front/f/6/f68e5337-6e44-4f8f-a102-2f97b433beea.jpg?1783923716"
        )
        timing = TimingRule.SorcerySpeed
        description = "Create a 2/2 white Samurai creature token with vigilance."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c47931c9-685d-4b83-8299-bc347224b4e8.jpg?1783923870"
    }
}
