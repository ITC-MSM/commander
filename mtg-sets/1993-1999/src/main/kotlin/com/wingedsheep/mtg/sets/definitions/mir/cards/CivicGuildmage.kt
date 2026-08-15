 package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Civic Guildmage
 * {W}
 * Creature — Human Wizard
 * 1/1
 *
 * {G}, {T}: Target creature gets +0/+1 until end of turn.
 * {U}, {T}: Put target creature you control on top of its owner's library.
 */
val CivicGuildmage = card("Civic Guildmage") {
    manaCost = "{W}"
    colorIdentity = "WUG"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "{G}, {T}: Target creature gets +0/+1 until end of turn.\n" +
        "{U}, {T}: Put target creature you control on top of its owner's library."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        val t = target("target creature", TargetCreature())
        effect = Effects.ModifyStats(0, 1, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        val t = target(
            "target creature you control",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.youControl()))
        )
        effect = Effects.PutOnTopOfLibrary(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Andrew Robinson"
        flavorText = "To condemn the innocent you must first condemn yourself.\n—Civic Guild maxim"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9319039-db2f-47bf-9ef0-8d3a381d54fb.jpg?1562720965"
    }
}
