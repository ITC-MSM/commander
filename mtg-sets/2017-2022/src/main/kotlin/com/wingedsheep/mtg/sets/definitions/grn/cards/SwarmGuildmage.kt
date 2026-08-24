package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Swarm Guildmage
 * {B}{G}
 * Creature — Elf Shaman
 * 2/2
 * {4}{B}, {T}: Creatures you control get +1/+0 and gain menace until end of turn. (They can't be blocked except by two or more creatures.)
 * {1}{G}, {T}: You gain 2 life.
 */
val SwarmGuildmage = card("Swarm Guildmage") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elf Shaman"
    oracleText = "{4}{B}, {T}: Creatures you control get +1/+0 and gain menace until end of turn. (They can't be blocked except by two or more creatures.)\n" +
        "{1}{G}, {T}: You gain 2 life."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{B}"), Costs.Tap)
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 1,
            toughness = 0,
            keyword = Keyword.MENACE,
            filter = GroupFilter.AllCreaturesYouControl
        )
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.Tap)
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "201"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/7599adc7-72b2-4079-ac0a-1a821f9de925.jpg?1783934123"
    }
}
