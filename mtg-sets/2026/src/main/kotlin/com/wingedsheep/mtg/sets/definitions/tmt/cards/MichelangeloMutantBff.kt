package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Michelangelo, Mutant BFF
 * {2}{G}{G}
 * Legendary Creature — Mutant Ninja Turtle
 * 4/4
 *
 * Each creature you control with a counter on it can't be blocked by more than one creature.
 * Whenever Michelangelo enters or attacks, create a Mutagen token.
 */
val MichelangeloMutantBff = card("Michelangelo, Mutant BFF") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "Each creature you control with a counter on it can't be blocked by more than one creature.\nWhenever Michelangelo enters or attacks, create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"
    power = 4
    toughness = 4

    staticAbility {
        ability = CantBeBlockedByMoreThan(
            maxBlockers = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl().withAnyCounter())
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateMutagenToken()
        description = "Whenever Michelangelo enters, create a Mutagen token."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.CreateMutagenToken()
        description = "Whenever Michelangelo attacks, create a Mutagen token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Narendra Bintara Adi"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02f7281f-b50c-4a1c-a2fc-3caeb6ab7d41.jpg?1771502715"
    }
}
