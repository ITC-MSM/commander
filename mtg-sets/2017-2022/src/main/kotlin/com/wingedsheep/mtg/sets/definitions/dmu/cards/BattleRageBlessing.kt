package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Battle-Rage Blessing
 * {1}{B}
 * Instant
 * Target creature gains deathtouch and indestructible until end of turn. (Damage and effects that say "destroy" don't destroy it.)
 */
val BattleRageBlessing = card("Battle-Rage Blessing") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gains deathtouch and indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, t),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Jarel Threat"
        flavorText = "\"Since when is it cheating to give one side an unfair advantage?\"\n—Braids"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7cfc631b-49d1-4f1c-adac-5b07a2ccd06f.jpg?1783921339"
    }
}
