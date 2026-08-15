package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dragonclaw Strike
 * {2/G}{2/U}{2/R}
 * Sorcery
 *
 * Double the power and toughness of target creature you control until end of turn.
 * Then it fights up to one target creature an opponent controls.
 * (Each deals damage equal to its power to the other.)
 *
 * "Doubling" is modelled per the Comprehensive Rules ruling: the creature gets +X/+X
 * where X is its current power/toughness as the effect applies, so we feed the target's
 * own power/toughness back in as the modification amount (handles negative power too,
 * since the dynamic amount reflects the live value). The fight reuses [Effects.Fight];
 * the opponent's creature is "up to one" so its target is optional.
 */
val DragonclawStrike = card("Dragonclaw Strike") {
    manaCost = "{2/G}{2/U}{2/R}"
    colorIdentity = "GUR"
    typeLine = "Sorcery"
    oracleText = "Double the power and toughness of target creature you control until end of turn. " +
        "Then it fights up to one target creature an opponent controls. " +
        "(Each deals damage equal to its power to the other.)"

    spell {
        val yourCreature = target(
            "creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl)
        )
        val opponentCreature = target(
            "creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls)
        )
        effect = Effects.ModifyStats(
            power = DynamicAmounts.targetPower(0),
            toughness = DynamicAmounts.targetToughness(0),
            target = yourCreature
        ).then(Effects.Fight(yourCreature, opponentCreature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Yeong-Hao Han"
        flavorText = "Some had doubted the whisperers' choice of Eshki for Dragonclaw. " +
            "None did so after witnessing her in battle."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc7692ef-7091-4365-85a8-1edbd374f279.jpg?1744577243"
    }
}
