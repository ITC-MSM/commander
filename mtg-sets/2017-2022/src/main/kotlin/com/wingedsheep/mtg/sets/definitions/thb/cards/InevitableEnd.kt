package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility

/**
 * Inevitable End
 * {2}{B}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "At the beginning of your upkeep, sacrifice a creature."
 *
 * The quoted ability is *granted to the enchanted creature* ([GrantTriggeredAbility] over the default
 * attached-creature filter) rather than printed on the Aura, which is what makes the quoted "your"
 * mean the enchanted creature's controller: the engine indexes a granted trigger on the permanent it
 * was granted to, so [Triggers.YourUpkeep]'s `Player.You` reads that permanent's controller. Printing
 * an ordinary triggered ability on the Aura instead would make the Aura's controller sacrifice —
 * wrong for a card whose whole point is enchanting someone else's creature. Relic Bane is the same
 * shape, and its scenario test pins the attribution.
 *
 * The granted ability keeps [Triggers.YourUpkeep]'s own `ANY` binding; an `ATTACHED` binding would
 * drop it out of the trigger index entirely.
 *
 * The sacrifice is [Effects.SacrificeOwn], not `Effects.Sacrifice`: the printed clause is the bare
 * imperative, so the ability's controller chooses and sacrifices one of their own creatures. There is
 * no named player and no target.
 */
val InevitableEnd = card("Inevitable End") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"At the beginning of your upkeep, sacrifice a creature.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YourUpkeep.event,
                binding = Triggers.YourUpkeep.binding,
                effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Josh Hass"
        flavorText = "\"How many more will die before you accept your fate?\"\n—Erebos, god of the dead"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6522f01-ae7a-467e-9a05-530a5ccd304d.jpg"
    }
}
