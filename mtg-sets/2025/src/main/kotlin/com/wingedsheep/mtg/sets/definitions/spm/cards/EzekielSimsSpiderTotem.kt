package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Ezekiel Sims, Spider-Totem
 * {4}{G}
 * Legendary Creature — Spider Human Advisor, 3/5
 * Reach
 * At the beginning of combat on your turn, target Spider you control gets +2/+2 until end of turn.
 */
val EzekielSimsSpiderTotem = card("Ezekiel Sims, Spider-Totem") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Spider Human Advisor"
    power = 3
    toughness = 5
    oracleText = "Reach\nAt the beginning of combat on your turn, target Spider you control gets +2/+2 until end of turn."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target(
            "target Spider you control",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withSubtype(Subtype.SPIDER).youControl()))
        )
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Wei Guan"
        flavorText = "For all his wealth and power, there is a fear deep inside Ezekiel that spurs him to train others."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb7c3ae2-6b01-4472-8bd1-9a7456401ddc.jpg?1757377421"
    }
}
