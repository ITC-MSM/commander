package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Goblin Negotiation
 * {X}{R}{R}
 * Sorcery
 *
 * Goblin Negotiation deals X damage to target creature. Create a number of 1/1 red Goblin
 * creature tokens equal to the amount of excess damage dealt to that creature this way.
 *
 * Composed from existing atoms (mirrors Hell to Pay): [Effects.DealXDamage] deals X to the target
 * and marks the damage, then [Effects.CreateToken] reads the post-damage excess via
 * `EntityProperty(EntityReference.Target(0), ExcessMarkedDamage)` — `max(0, marked − toughness)`
 * (CR 120.4a). CompositeEffect resolves its steps sequentially with no interleaved SBA pass, so
 * the marked damage in scope at the second step is exactly the X this spell just dealt.
 */
val GoblinNegotiation = card("Goblin Negotiation") {
    manaCost = "{X}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Goblin Negotiation deals X damage to target creature. Create a number of 1/1 " +
        "red Goblin creature tokens equal to the amount of excess damage dealt to that creature this way."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealXDamage(creature),
            Effects.CreateToken(
                count = DynamicAmount.EntityProperty(
                    EntityReference.Target(0),
                    EntityNumericProperty.ExcessMarkedDamage
                ),
                power = 1,
                toughness = 1,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Goblin"),
                imageUri = "https://cards.scryfall.io/normal/front/7/0/70f8a1de-cd4c-4afa-bf03-0245d375d42e.jpg?1782727474"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "Svetlin Velinov"
        flavorText = "Either you pay for Krenko's \"protection\" or you pay to replace your stall. Either way, you pay."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2016585-e26c-4d13-b09f-af6383c192f7.jpg?1782689188"

        ruling("2024-11-15", "Excess damage is the amount of damage dealt to the creature beyond what was needed to be lethal. Damage already marked on the creature and any damage that would be prevented are taken into account.")
    }
}
