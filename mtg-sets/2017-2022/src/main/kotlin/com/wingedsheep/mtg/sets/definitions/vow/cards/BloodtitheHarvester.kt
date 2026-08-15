package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bloodtithe Harvester
 * {B}{R}
 * Creature — Vampire
 * 3/2
 * When this creature enters, create a Blood token.
 * {T}, Sacrifice this creature: Target creature gets -X/-X until end of turn, where X is twice
 * the number of Blood tokens you control. Activate only as a sorcery.
 */
val BloodtitheHarvester = card("Bloodtithe Harvester") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, create a Blood token. (It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "{T}, Sacrifice this creature: Target creature gets -X/-X until end of turn, where X is twice the number of Blood tokens you control. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateBlood()
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        val t = target("target creature", TargetCreature(filter = TargetFilter.Creature))
        // X is twice the number of Blood tokens you control; apply as -X/-X.
        val bloodCount = DynamicAmounts.battlefield(
            Player.You,
            GameObjectFilter.Artifact.withSubtype("Blood")
        ).count()
        val negTwiceBlood = DynamicAmount.Multiply(bloodCount, -2)
        effect = Effects.ModifyStats(power = negTwiceBlood, toughness = negTwiceBlood, target = t)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "Lucas Graciano"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0192cf7-3391-4720-b9c8-72dec5dde01e.jpg?1782703028"
    }
}
