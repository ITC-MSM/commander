package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Shriekmaw
 * {4}{B}
 * Creature — Elemental
 * 3/2
 * Fear
 * When this creature enters, destroy target nonartifact, nonblack creature.
 * Evoke {1}{B}
 *
 * Same shape as Mulldrifter: `evoke = "{1}{B}"` is the alternative cost and the engine adds the
 * "sacrifice it when it enters" trigger itself. The destroy trigger is a plain enters trigger with a
 * nonartifact-nonblack target; when evoked, both triggers go on the stack together and the
 * controller orders them (the 2021-03-19 ruling).
 */
val Shriekmaw = card("Shriekmaw") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 2
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or black " +
        "creatures.)\n" +
        "When this creature enters, destroy target nonartifact, nonblack creature.\n" +
        "Evoke {1}{B} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    keywords(Keyword.FEAR)

    evoke = "{1}{B}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target nonartifact, nonblack creature",
            TargetCreature(filter = TargetFilter.Creature.nonartifact().notColor(Color.BLACK)),
        )
        effect = Effects.Destroy(t)
        description = "destroy target nonartifact, nonblack creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "139"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea19eb5e-0d70-42e6-bda2-b13757e08ca6.jpg?1783942884"
        ruling("2021-03-19", "If you pay the evoke cost, you can have Shriekmaw's own triggered ability resolve before the evoke triggered ability. You can cast spells after that ability resolves but before you have to sacrifice Shriekmaw.")
    }
}
