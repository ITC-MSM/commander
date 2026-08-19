package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Tar Pitcher
 * {3}{R}
 * Creature — Goblin Shaman
 * 2/2
 * {T}, Sacrifice a Goblin: This creature deals 2 damage to any target.
 *
 * Tar Pitcher is itself a Goblin, so it can be the Goblin sacrificed — [Costs.Sacrifice], not
 * `SacrificeAnother`. The damage still happens: the ability is on the stack independently of its
 * source.
 */
val TarPitcher = card("Tar Pitcher") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "{T}, Sacrifice a Goblin: This creature deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN))
        )
        val recipient = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, recipient)
        description = "{T}, Sacrifice a Goblin: This creature deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "193"
        artist = "Omar Rayyan"
        flavorText = "\"Auntie Grub had caught a goat, a bleating doe that still squirmed in her arms. At the same time, her warren decided to share with her a sensory greeting of hot tar . . . .\"\n—A tale of Auntie Grub"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ffd2ec11-eb65-488d-a857-b5020113c429.jpg?1783942870"
    }
}
