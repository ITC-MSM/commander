package com.wingedsheep.mtg.sets.definitions.mmq.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Arms Dealer
 * {2}{R}
 * Creature — Goblin Rogue
 * 1/1
 * {1}{R}, Sacrifice a Goblin: This creature deals 4 damage to target creature.
 *
 * "Sacrifice a Goblin" is [Costs.Sacrifice] over any *permanent* with the Goblin subtype — not
 * `SacrificeAnother` — so Arms Dealer itself is a legal sacrifice for its own ability.
 */
val ArmsDealer = card("Arms Dealer") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "{1}{R}, Sacrifice a Goblin: This creature deals 4 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{R}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype("Goblin"))
        )
        val t = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "172"
        artist = "Luca Zontini"
        flavorText = "He has the weapons, he has the connections, and he knows the hangar's dark secret."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dafea45d-be00-428d-a127-70e6a14efe3f.jpg?1783945943"
        ruling("2012-07-01", "You can sacrifice Arms Dealer to activate its own ability.")
    }
}
