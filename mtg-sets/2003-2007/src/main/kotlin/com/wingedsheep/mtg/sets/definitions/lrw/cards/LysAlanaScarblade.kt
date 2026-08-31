package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lys Alana Scarblade
 * {2}{B}
 * Creature — Elf Assassin
 * 1/1
 * {T}, Discard an Elf card: Target creature gets -X/-X until end of turn, where X is the number of
 * Elves you control.
 *
 * X is an `AggregateBattlefield` tally read on resolution, not on activation — the 2016-06-08 ruling
 * is explicit that a Scarblade still on the battlefield counts itself, and that the count is taken
 * as the ability resolves. `Multiply(…, -1)` is how the SDK spells a shrink; the same amount serves
 * both halves because the card shrinks power and toughness equally.
 *
 * The two filters deliberately differ. The cost discards an Elf *card* from hand, so it is
 * `GameObjectFilter.Any` — that admits the Kindred noncreature Elf cards (Gilt-Leaf Ambush, Elvish
 * Promenade) alongside Elf creatures. The tally counts Elves on the battlefield, so it is
 * `GameObjectFilter.Permanent`.
 */
val LysAlanaScarblade = card("Lys Alana Scarblade") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Assassin"
    power = 1
    toughness = 1
    oracleText = "{T}, Discard an Elf card: Target creature gets -X/-X until end of turn, where X is the number of Elves you control."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.Discard(filter = GameObjectFilter.Any.withSubtype(Subtype.ELF)),
        )
        val victim = target("target creature", Targets.Creature)
        val shrink = DynamicAmount.Multiply(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Permanent.withSubtype(Subtype.ELF),
            ),
            -1,
        )
        effect = Effects.ModifyStats(shrink, shrink, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "Christopher Moeller"
        flavorText = "In beauty-obsessed Lys Alana, one cut of her blade means the difference between a high society feast and raking through the dungheap for scraps."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/7614e3e7-708f-4717-b798-97f0c391a673.jpg?1783942889"
        ruling("2016-06-08", "The number of Elves you control is counted only as Lys Alana Scarblade's ability resolves. If Lys Alana Scarblade is still on the battlefield, it'll count itself.")
    }
}
