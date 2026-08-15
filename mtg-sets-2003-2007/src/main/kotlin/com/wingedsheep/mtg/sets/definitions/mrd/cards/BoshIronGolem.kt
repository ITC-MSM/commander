package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Bosh, Iron Golem — Mirrodin #147
 * {8} · Legendary Artifact Creature — Golem · 6/7
 *
 * Trample
 * {3}{R}, Sacrifice an artifact: Bosh deals damage equal to the sacrificed artifact's mana value
 * to any target.
 *
 * The textbook sacrifice-cost-feeds-the-effect shape, and it needs no engine work: the
 * [Costs.Sacrifice] cost binds the sacrificed artifact to [EntityReference.Sacrificed], whose
 * last-known information is captured at cost payment, and the damage amount reads
 * [EntityNumericProperty.ManaValue] off that snapshot (same wiring as Priest of Yawgmoth).
 *
 * Two rulings ride on that:
 *  - Bosh is itself an artifact and the filter doesn't exclude the source, so **Bosh can be
 *    sacrificed to its own ability** — the damage is still dealt, from the graveyard, using
 *    last-known information (CR 608.2h).
 *  - An {X} in an artifact's mana cost is 0 on the battlefield (CR 202.3b), so sacrificing an
 *    X-cost artifact deals damage equal to the rest of its cost only.
 */
val BoshIronGolem = card("Bosh, Iron Golem") {
    manaCost = "{8}"
    colorIdentity = "R"
    typeLine = "Legendary Artifact Creature — Golem"
    power = 6
    toughness = 7
    oracleText = "Trample\n" +
        "{3}{R}, Sacrifice an artifact: Bosh deals damage equal to the sacrificed artifact's " +
        "mana value to any target."

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{R}"), Costs.Sacrifice(GameObjectFilter.Artifact))
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(
            DynamicAmount.EntityProperty(
                EntityReference.Sacrificed(0),
                EntityNumericProperty.ManaValue
            ),
            t
        )
        description = "{3}{R}, Sacrifice an artifact: Bosh deals damage equal to the sacrificed " +
            "artifact's mana value to any target."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "147"
        artist = "Brom"
        flavorText = "As Glissa searches for the truth about Memnarch, Bosh searches to unearth " +
            "the secrets of his past."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9bfe325c-8d3d-4543-9fcd-214525d4ab2a.jpg?1783944527"
        ruling("2020-08-07", "Bosh can be sacrificed to pay the cost of its last ability.")
        ruling("2020-08-07", "If an artifact on the battlefield has {X} in its mana cost, X is considered to be 0.")
    }
}
