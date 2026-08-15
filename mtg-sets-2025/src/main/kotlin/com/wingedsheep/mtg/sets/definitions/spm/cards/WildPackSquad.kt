package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wild Pack Squad
 * {2}{W}
 * Creature — Human Mercenary
 * 2/3
 * At the beginning of combat on your turn, up to one target creature gains first strike and vigilance until end of turn.
 */
val WildPackSquad = card("Wild Pack Squad") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Mercenary"
    oracleText = "At the beginning of combat on your turn, up to one target creature gains first strike and vigilance until end of turn."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target("target", TargetCreature(optional = true, filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
            Effects.GrantKeyword(Keyword.VIGILANCE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "John Tyler Christopher"
        flavorText = "Silver Sable doesn't accept second best."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b0eda7c-e44d-4d9b-9042-4a1eb8c4ed4a.jpg?1757376883"
    }
}
