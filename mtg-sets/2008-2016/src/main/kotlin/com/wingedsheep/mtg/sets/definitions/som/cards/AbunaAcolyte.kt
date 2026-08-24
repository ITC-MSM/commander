package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Abuna Acolyte
 * {1}{W}
 * Creature — Cat Cleric
 * 1/1
 *
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 * {T}: Prevent the next 2 damage that would be dealt to target artifact creature this turn.
 *
 * Two independent tap abilities, each a plain [Effects.PreventNextDamage] shield — the same
 * primitive Samite Healer and Argivian Blacksmith use, differing only in the shield size and in
 * which target requirement declares the recipient.
 */
val AbunaAcolyte = card("Abuna Acolyte") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Cleric"
    power = 1
    toughness = 1
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to any target this turn.\n" +
        "{T}: Prevent the next 2 damage that would be dealt to target artifact creature this turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Any)
        effect = Effects.PreventNextDamage(1, t)
        description = "{T}: Prevent the next 1 damage that would be dealt to any target this turn."
    }

    activatedAbility {
        cost = Costs.Tap
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.ArtifactCreature))
        )
        effect = Effects.PreventNextDamage(2, t)
        description = "{T}: Prevent the next 2 damage that would be dealt to target artifact creature this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Igor Kieryluk"
        flavorText = "\"You can break nothing I cannot mend.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e17bbf7-00c0-46f2-9718-2762fd7388d3.jpg?1783941748"
    }
}
