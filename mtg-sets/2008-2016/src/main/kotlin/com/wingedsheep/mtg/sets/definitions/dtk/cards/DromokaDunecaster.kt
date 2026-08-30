package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dromoka Dunecaster
 * {W}
 * Creature — Human Wizard
 * 0 / 2
 *
 * {1}{W}, {T}: Tap target creature without flying.
 *
 * The printed cost is two atoms in one line, so it is a [Costs.Composite] of the mana payment and
 * the tap symbol. "Without flying" is a *targeting* restriction rather than anything the effect
 * checks, so it lives on the target filter and is re-checked against projected state both on
 * activation and on resolution (cf. Merfolk Seastalkers, Flood).
 */
val DromokaDunecaster = card("Dromoka Dunecaster") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    power = 0
    toughness = 2
    oracleText = "{1}{W}, {T}: Tap target creature without flying."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING)))
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Mark Winters"
        flavorText = "\"The dragonlords rule the tempests of the skies. Here in the wastes, the storms are mine to command.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc98e542-4bcc-43e1-9616-64ce40348a34.jpg?1783938618"
    }
}
