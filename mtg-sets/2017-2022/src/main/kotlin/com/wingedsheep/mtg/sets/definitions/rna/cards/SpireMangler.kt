package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spire Mangler — Ravnica Allegiance #86
 * {2}{B} · Creature — Insect · 2 / 1
 *
 * The target is narrowed to your own fliers, so it needs a filtered [TargetCreature] rather
 * than the shared [com.wingedsheep.sdk.dsl.Targets.CreatureYouControl]. The keyword test reads
 * projected state, so a creature granted flying this turn is a legal target.
 */
val SpireMangler = card("Spire Mangler") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 2
    toughness = 1
    oracleText = "Flash\n" +
        "Flying\n" +
        "When this creature enters, target creature you control with flying gets +2/+0 until end of turn."

    keywords(Keyword.FLASH, Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val flier = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING).youControl()))
        )
        effect = Effects.ModifyStats(2, 0, flier)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Tomasz Jedruszek"
        flavorText = "Its mandibles can leave a rider in the clouds astride a headless griffin."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3ce548d-764a-4397-bae3-d348dca78421.jpg"
    }
}
