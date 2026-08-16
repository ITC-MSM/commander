package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Woodfall Primus
 * {5}{G}{G}{G}
 * Creature — Treefolk Shaman
 * 6/6
 *
 * Trample
 * When this creature enters, destroy target noncreature permanent.
 * Persist
 *
 * Persist is engine-live ([Keyword.PERSIST] is read by the death/leave trigger detector), so the
 * keyword alone carries the behaviour — and the returning body brings the ETB destroy with it.
 */
val WoodfallPrimus = card("Woodfall Primus") {
    manaCost = "{5}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Shaman"
    power = 6
    toughness = 6
    oracleText = "Trample\n" +
        "When this creature enters, destroy target noncreature permanent.\n" +
        "Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the " +
        "battlefield under its owner's control with a -1/-1 counter on it.)"

    keywords(Keyword.TRAMPLE, Keyword.PERSIST)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetPermanent(filter = TargetFilter.NoncreaturePermanent)
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
        description = "When this creature enters, destroy target noncreature permanent."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43aa7e35-55ee-4e02-a8aa-ea2b267055d1.jpg"
    }
}
