package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Withstand Death — Scars of Mirrodin #134
 * {G} · Instant
 *
 * Target creature gains indestructible until end of turn.
 *
 * [Effects.GrantKeyword] with the default [com.wingedsheep.sdk.scripting.Duration.EndOfTurn] — the
 * grant is a keyword, not a damage-prevention shield, so it survives lethal damage and "destroy"
 * alike while still letting the creature die to 0 toughness or to sacrifice. The target is any
 * creature, not only one you control.
 */
val WithstandDeath = card("Withstand Death") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it. If its toughness is 0 or less, it still dies.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Tomasz Jedruszek"
        flavorText = "On Mirrodin, every conflict ends in either death or darksteel."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b059cca0-2373-428b-a3a6-c8be5523c96f.jpg?1783941715"
    }
}
