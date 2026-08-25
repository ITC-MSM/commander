package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Pashalik Mons
 * {2}{R}
 * Legendary Creature — Goblin Warrior
 * 2/2
 * Whenever Pashalik Mons or another Goblin you control dies, Pashalik Mons deals 1 damage to any target.
 * {3}{R}, Sacrifice a Goblin: Create two 1/1 red Goblin creature tokens.
 *
 * "This or another Goblin you control" is the ANY binding — Pashalik Mons is inside its own
 * trigger's scope. The bare tribal noun "Goblin" names every *permanent* with the subtype, in the
 * trigger filter and in the sacrifice cost alike (cf. Sling-Gang Lieutenant).
 */
val PashalikMons = card("Pashalik Mons") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Goblin Warrior"
    power = 2
    toughness = 2
    oracleText = "Whenever Pashalik Mons or another Goblin you control dies, Pashalik Mons deals 1 damage to any target.\n" +
        "{3}{R}, Sacrifice a Goblin: Create two 1/1 red Goblin creature tokens."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Goblin").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
        description = "Whenever Pashalik Mons or another Goblin you control dies, Pashalik Mons deals 1 damage to any target."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{R}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype("Goblin"))
        )
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            imageUri = "https://cards.scryfall.io/normal/front/7/0/70f8a1de-cd4c-4afa-bf03-0245d375d42e.jpg?1782727474",
        )
        description = "{3}{R}, Sacrifice a Goblin: Create two 1/1 red Goblin creature tokens."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "138"
        artist = "Even Amundsen"
        flavorText = "The thunderhead that leads in the storm."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11616853-34b1-4bb1-9590-461e12970ec3.jpg?1783933110"
    }
}
