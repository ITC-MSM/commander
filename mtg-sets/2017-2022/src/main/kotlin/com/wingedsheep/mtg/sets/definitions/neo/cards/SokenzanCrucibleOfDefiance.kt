package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sokenzan, Crucible of Defiance — Kamigawa: Neon Dynasty #276 (canonical printing)
 * Legendary Land · Rare
 *
 * {T}: Add {R}.
 * Channel — {3}{R}, Discard this card: Create two 1/1 colorless Spirit creature tokens. They
 * gain haste until end of turn. This ability costs {1} less to activate for each legendary
 * creature you control.
 *
 * One of the five NEO channel lands; see [OtawaraSoaringCity] for the shape they share.
 *
 * "They gain haste until end of turn" iterates the [CREATED_TOKENS] pipeline collection the
 * create-token executor publishes, so the grant lands on exactly the two tokens this activation
 * made — and only until end of turn (the [Effects.GrantKeyword] default). Baking haste into the
 * token itself would be the easy approximation and the wrong one: printed haste would still be
 * there on a later turn, which matters the moment the token changes control.
 */
val SokenzanCrucibleOfDefiance = card("Sokenzan, Crucible of Defiance") {
    typeLine = "Legendary Land"
    colorIdentity = "R"
    oracleText = "{T}: Add {R}.\n" +
        "Channel — {3}{R}, Discard this card: Create two 1/1 colorless Spirit creature tokens. " +
        "They gain haste until end of turn. This ability costs {1} less to activate for each " +
        "legendary creature you control."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
    }

    // Channel — {3}{R}, Discard this card (from hand): two 1/1 Spirits with haste until EOT.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{R}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        genericCostReduction = DynamicAmounts.legendaryCreaturesYouControl()
        effect = Effects.Composite(
            listOf(
                Effects.CreateToken(
                    count = 2,
                    power = 1,
                    toughness = 1,
                    colors = emptySet(),
                    creatureTypes = setOf("Spirit"),
                    imageUri = "https://cards.scryfall.io/normal/front/c/a/ca20548f-6324-4858-adbe-87303ff1ca52.jpg?1783923715"
                ),
                ForEachInCollectionEffect(
                    CREATED_TOKENS,
                    Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Lucas Staniec"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa548dcd-c1dd-492d-a69f-c65dfeef0633.jpg?1783923814"
    }
}
