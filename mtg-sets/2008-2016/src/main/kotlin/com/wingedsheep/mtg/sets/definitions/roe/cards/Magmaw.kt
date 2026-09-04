package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Magmaw
 * {3}{R}{R}
 * Creature — Elemental
 * 4 / 4
 *
 * {1}, Sacrifice a nonland permanent: This creature deals 1 damage to any target.
 *
 * Modeling notes:
 *  - Two cost components joined by a comma are one [Costs.Composite]: the mana half is
 *    `Costs.Mana("{1}")` and the sacrifice half is a [Costs.Sacrifice] over
 *    [GameObjectFilter.NonlandPermanent] — the filter whose card predicates are exactly the
 *    `IsNonland` + `IsPermanent` pair Assay compiles this line to. The permanent is chosen and
 *    sacrificed on activation, before the ability goes on the stack.
 *  - A sacrifice cost can only ever take permanents its payer controls, so the filter carries no
 *    controller predicate — the cost atom's candidate domain is already scoped to the payer.
 *  - The printed line says "a nonland permanent", **not** "another": [Costs.Sacrifice] (not
 *    `SacrificeAnother`) leaves Magmaw itself in the candidate pool, so feeding Magmaw to its own
 *    ability is a legal activation. The damage still happens; last-known information covers the
 *    source having left the battlefield.
 *  - "any target" is [Targets.Any] — creature, player, planeswalker or battle — and the damage is
 *    a plain [Effects.DealDamage] with the source defaulting to this creature, matching the printed
 *    "This creature deals".
 */
val Magmaw = card("Magmaw") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 4
    oracleText = "{1}, Sacrifice a nonland permanent: This creature deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Sacrifice(GameObjectFilter.NonlandPermanent)
        )
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "158"
        artist = "Karl Kopinski"
        flavorText = "\"The purpose of existence is simple: everything is fuel for the magmaw.\"\n—Jaji, magmaw worshipper"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/015de202-e796-4194-a06b-d74230759f38.jpg?1783941972"
    }
}
