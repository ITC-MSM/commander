package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Starry-Eyed Skyrider — Tarkir: Dragonstorm #25
 * {2}{W} · Creature — Human Scout · 1/3
 *
 * Flying
 * Whenever this creature attacks, another target creature you control gains flying until end of turn.
 * Attacking tokens you control have flying.
 *
 * The attack trigger grants flying to a single other creature you control via [Effects.GrantKeyword]
 * (until end of turn). The third line is a continuous static [GrantKeyword] over the group of
 * attacking tokens you control — composed from [GameObjectFilter.Token] + `.youControl()` +
 * `.attacking()`, so the flying is granted/removed dynamically by projected state as tokens enter or
 * leave combat (and lost immediately if Skyrider leaves the battlefield).
 */
val StarryEyedSkyrider = card("Starry-Eyed Skyrider") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever this creature attacks, another target creature you control gains flying until end of turn.\n" +
        "Attacking tokens you control have flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target("another creature you control", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.FLYING,
            filter = GroupFilter(GameObjectFilter.Token.youControl().attacking())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "Lindsey Look"
        flavorText = "Too late, the elders realized the young prodigies shared a love of adventure and mischief."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b3cc15e-1c82-454e-b541-4ab47c44814e.jpg?1743204050"
        ruling("2025-04-04", "If Starry-Eyed Skyrider leaves the battlefield, any attacking tokens will no longer be granted flying by the last ability. If that happens before the declare blockers step, it may allow those tokens to be blocked by creatures without flying.")
    }
}
