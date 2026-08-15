package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Splinter, Radical Rat
 * {1}{W/B}{W/B}
 * Legendary Creature — Mutant Ninja Rat
 * 2/4
 *
 * If a triggered ability of a Ninja creature you control triggers,
 * that ability triggers an additional time.
 * {1}{U}: Target Ninja can't be blocked this turn.
 *
 * Mirrors the Lorwyn Eclipsed Twinflame Travelers shape — the
 * trigger-doubler is just `AdditionalSourceTriggers` parameterised
 * on the Ninja subtype. The printed wording is "a Ninja creature you
 * control" (no "another"), so Splinter's own triggered abilities
 * would also be doubled if he had any; `excludeSelf = false` reflects
 * the wording faithfully.
 */
val SplinterRadicalRat = card("Splinter, Radical Rat") {
    manaCost = "{1}{W/B}{W/B}"
    colorIdentity = "WBU"
    typeLine = "Legendary Creature — Mutant Ninja Rat"
    oracleText = "If a triggered ability of a Ninja creature you control triggers, that ability triggers an additional time.\n{1}{U}: Target Ninja can't be blocked this turn."
    power = 2
    toughness = 4

    staticAbility {
        ability = AdditionalSourceTriggers(
            sourceFilter = GameObjectFilter.Creature.withSubtype("Ninja").youControl(),
            excludeSelf = false,
            description = "If a triggered ability of a Ninja creature you control triggers, that ability triggers an additional time"
        )
    }

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        val ninja = target(
            "target Ninja",
            TargetCreature(filter = TargetFilter.Creature.withSubtype("Ninja"))
        )
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, ninja)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "169"
        artist = "Manuel Castañón"
        flavorText = "\"The first rule of the ninja is 'do no harm.' Unless you need to do harm. Then do lots of harm!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0797466-c527-4d35-86bc-e0e90fd04073.jpg?1769024412"
    }
}
