package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Elvish Branchbender
 * {2}{G}
 * Creature — Elf Druid
 * 2/2
 *
 * {T}: Until end of turn, target Forest becomes an X/X Treefolk creature in addition to its other
 * types, where X is the number of Elves you control.
 *
 * X is locked in when the ability resolves (CR 613.4c) — the 2007-10-01 ruling says the animated
 * Forest keeps that size even if Elves die afterwards — so the count goes in `power`/`toughness`
 * rather than the continuously-re-evaluated `dynamicPower`/`dynamicToughness` pair.
 *
 * The bare noun "Elves" counts every Elf *permanent* you control, not only the creatures, and the
 * Branchbender is itself an Elf, so it counts itself.
 *
 * "In addition to its other types" is [Effects.BecomeCreature]'s default: it adds CREATURE and the
 * Treefolk subtype without removing LAND or the Forest subtype, so the animated land still taps
 * for {G}. Any Forest is a legal target, including an opponent's.
 */
val ElvishBranchbender = card("Elvish Branchbender") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 2
    toughness = 2
    oracleText = "{T}: Until end of turn, target Forest becomes an X/X Treefolk creature in " +
        "addition to its other types, where X is the number of Elves you control."

    activatedAbility {
        cost = Costs.Tap
        val forest = target(
            "target Forest",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.withSubtype(Subtype.FOREST)))
        )
        val elves = DynamicAmounts.battlefield(
            Player.You,
            GameObjectFilter.Permanent.withSubtype(Subtype.ELF)
        ).count()
        effect = Effects.BecomeCreature(
            target = forest,
            power = elves,
            toughness = elves,
            creatureTypes = setOf("Treefolk"),
            duration = Duration.EndOfTurn,
        )
        description = "{T}: Until end of turn, target Forest becomes an X/X Treefolk creature in " +
            "addition to its other types, where X is the number of Elves you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "204"
        artist = "Ralph Horsley"
        flavorText = "\"How do the vinebred feel? Fah! We do not ask the puppet how it feels when " +
            "the puppeteer bids it dance.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/1/5154fc12-a8f2-4a16-8b1f-f7415c87566d.jpg?1783942866"
    }
}
