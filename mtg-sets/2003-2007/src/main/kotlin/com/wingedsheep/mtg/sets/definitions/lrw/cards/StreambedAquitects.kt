package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Streambed Aquitects
 * {1}{U}{U}
 * Creature — Merfolk Scout
 * 2/3
 * {T}: Target Merfolk creature gets +1/+1 and gains islandwalk until end of turn.
 * {T}: Target land becomes an Island until end of turn.
 *
 * Two independently-targeted tap abilities that read as one plan: the second makes the defender's
 * land an Island so the first's islandwalk actually turns off blocking. "Becomes an Island"
 * *replaces* the land's subtypes (CR 305.7), hence [Effects.SetLandType] and not the additive
 * `AddSubtype` — the same distinction Dream Thrush documents.
 */
val StreambedAquitects = card("Streambed Aquitects") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Scout"
    power = 2
    toughness = 3
    oracleText = "{T}: Target Merfolk creature gets +1/+1 and gains islandwalk until end of turn. " +
        "(It can't be blocked as long as defending player controls an Island.)\n" +
        "{T}: Target land becomes an Island until end of turn."

    activatedAbility {
        val merfolk = target(
            "target Merfolk creature",
            TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.MERFOLK))
        )
        cost = AbilityCost.Tap
        effect = Effects.ModifyStats(1, 1, merfolk) then
            Effects.GrantKeyword(Keyword.ISLANDWALK, merfolk)
        description = "{T}: Target Merfolk creature gets +1/+1 and gains islandwalk until end of turn."
    }

    activatedAbility {
        val land = target("target land", Targets.Land)
        cost = AbilityCost.Tap
        effect = Effects.SetLandType(
            landType = "Island",
            target = land,
            duration = Duration.EndOfTurn
        )
        description = "{T}: Target land becomes an Island until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "William O'Connor"
        flavorText = "\"We look in the river and see scattered stones. A merrow looks and sees a " +
            "map of Lorwyn.\"\n—Illulia, flamekin soulstoke"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aed908f4-630c-4f3c-9e61-4725b88b93ff.jpg?1783942896"
    }
}
