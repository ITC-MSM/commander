package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CreateAdditionalToken
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Quina, Qu Gourmet
 * {2}{G}
 * Legendary Creature — Qu
 * 2/3
 *
 * If one or more tokens would be created under your control, those tokens plus a
 * 1/1 green Frog creature token are created instead.
 * {2}, Sacrifice a Frog: Put a +1/+1 counter on Quina.
 *
 * The token-creation clause is a replacement effect (CR 614) that fires once per
 * token-creation event under your control, regardless of how many or what kind of
 * tokens the event makes (Scryfall rulings 2025-06-06: you must be the one creating
 * the tokens, but you needn't control the source). It is self-limiting (CR 614.5):
 * the added Frog is created directly and does not itself re-enter the replacement
 * pipeline, so it never recurses on its own Frog. The added Frog also gains none of
 * the original tokens' abilities (ruling), though it does inherit the original
 * creation's "tapped" rider via [CreateAdditionalToken.inheritTapped].
 */
val QuinaQuGourmet = card("Quina, Qu Gourmet") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Qu"
    power = 2
    toughness = 3
    oracleText = "If one or more tokens would be created under your control, those tokens plus a 1/1 green Frog creature token are created instead.\n" +
        "{2}, Sacrifice a Frog: Put a +1/+1 counter on Quina."

    // "those tokens plus a 1/1 green Frog creature token are created instead." The engine
    // creates the extra Frog directly after the primary tokens, without re-entering the
    // token-creation replacement pipeline, so it never recurses on its own added Frog
    // (CR 614.5). The default appliesTo (You / any token) matches every token created
    // under your control.
    replacementEffect(CreateAdditionalToken(additionalTokenType = "Frog"))

    // "{2}, Sacrifice a Frog: Put a +1/+1 counter on Quina."
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Sacrifice(GameObjectFilter.Creature.withSubtype("Frog"))
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "194"
        artist = "Fajareka Setiawan"
        flavorText = "\"World only have two things: Things you can eat and things you no can eat.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f352b5e-9731-4a8e-b872-db5d3bf32211.jpg?1748706489"
    }
}
