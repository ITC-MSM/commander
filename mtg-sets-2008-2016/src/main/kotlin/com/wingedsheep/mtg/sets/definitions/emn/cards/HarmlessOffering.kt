package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Harmless Offering
 * {2}{R}
 * Sorcery
 *
 * Target opponent gains control of target permanent you control.
 *
 * Two targets, declared in oracle order: the opponent who receives control (context 0) and the
 * permanent you control that changes hands (context 1). The permanent moves permanently — the
 * classic "donate" gift, weaponized with cards that punish their new controller.
 */
val HarmlessOffering = card("Harmless Offering") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Target opponent gains control of target permanent you control."

    spell {
        val opponent = target("opponent", TargetOpponent())
        val permanent = target("permanent", TargetPermanent(filter = TargetFilter.PermanentYouControl))
        effect = GiveControlToTargetPlayerEffect(
            permanent = permanent,
            newController = opponent
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "131"
        artist = "Howard Lyon"
        flavorText = "\"Such an adorable face!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8f3cc4f-7943-4025-b332-b40653b13014.jpg?1783937459"
    }
}
