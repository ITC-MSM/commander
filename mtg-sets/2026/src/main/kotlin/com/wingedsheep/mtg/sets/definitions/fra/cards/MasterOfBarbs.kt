package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val MasterOfBarbs = card("Master of Barbs") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Bard"
    power = 2
    toughness = 1
    oracleText = "Menace\nWhenever one or more opponents are dealt noncombat damage, creatures you control get +1/+0 until end of turn."

    keywords(Keyword.MENACE)

    // "One or more" batches simultaneous damage (CR 603.2c): a spell that hits two opponents at
    // once triggers this once.
    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.NonCombat,
            recipient = RecipientFilter.Opponent,
            binding = TriggerBinding.ANY,
            batch = true,
        )
        effect = Patterns.Group.modifyStatsForAll(1, 0, GroupFilter(GameObjectFilter.Creature.youControl()))
        description = "Whenever one or more opponents are dealt noncombat damage, creatures you control get +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Aldo Domínguez"
        flavorText = "\"The question isn't *if* he'll make his students cry. Only when.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/4/4404d9d4-9cdd-4dad-a4f6-574d90db5052.jpg?1789127596"
        inBooster = false
    }
}
