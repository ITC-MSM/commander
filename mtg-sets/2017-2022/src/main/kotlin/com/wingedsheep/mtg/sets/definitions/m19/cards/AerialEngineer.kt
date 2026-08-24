package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Aerial Engineer
 * {2}{W}{U}
 * Creature — Human Artificer
 * 2/4
 * As long as you control an artifact, this creature gets +2/+0 and has flying.
 *
 * One printed sentence, two statics: the layer-7c [ModifyStats] and the layer-6 [GrantKeyword]
 * live in different layers (CR 613.1), so they are two [ConditionalStaticAbility] entries over the
 * same condition rather than one composite. Both target `GroupFilter.source()` — the permanent
 * itself — and the shared gate is an existence check on the battlefield, recomputed at projection
 * so the bonus comes and goes with the artifact.
 */
val AerialEngineer = card("Aerial Engineer") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 4
    oracleText = "As long as you control an artifact, this creature gets +2/+0 and has flying."

    val controlAnArtifact = Conditions.YouControl(GameObjectFilter.Artifact)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = controlAnArtifact,
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = controlAnArtifact,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Zoltan Boros"
        flavorText = "The best of their trade know every bolt of their rigs, stem to stern."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5314bae2-4930-4f8a-8a52-853bc3feb88f.jpg"
    }
}
