package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Abundant Growth
 * {G}
 * Enchantment — Aura
 * Enchant land
 * When this Aura enters, draw a card.
 * Enchanted land has "{T}: Add one mana of any color."
 *
 * The raw [ActivatedAbility] constructor defaults to `isManaAbility = false`, so the grant sets
 * both that flag and [TimingRule.ManaAbility] explicitly — otherwise the enchanted land's granted
 * ability would use the stack and couldn't be activated while paying for a spell.
 */
val AbundantGrowth = card("Abundant Growth") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant land\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted land has \"{T}: Add one mana of any color.\""

    auraTarget = Targets.Land

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddAnyColorMana(1),
                isManaAbility = true,
                timing = TimingRule.ManaAbility
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "Vincent Proce"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afbc8fd0-dc15-4ac9-b97b-173f7fb66ed7.jpg?1782714455"
    }
}
