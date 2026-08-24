package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Embraal Bruiser
 * {1}{B}
 * Creature — Human Warrior
 * 3 / 1
 *
 * This creature enters tapped.
 * This creature has menace as long as you control an artifact.
 *
 * The tapped entry is a replacement effect ([EntersTapped]), not a trigger — it changes how the
 * permanent arrives rather than reacting to its arrival. Menace is a [ConditionalStaticAbility]
 * wrapping [GrantKeyword] over [GroupFilter.source], gated by an [Exists] check for an artifact
 * you control; that condition is re-read continuously in Layer 6, so menace comes and goes with
 * the artifact rather than being locked in on arrival.
 */
val EmbraalBruiser = card("Embraal Bruiser") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    oracleText = "This creature enters tapped.\n" +
        "This creature has menace as long as you control an artifact."
    power = 3
    toughness = 1

    replacementEffect(EntersTapped())

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.MENACE, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "79"
        artist = "Sidharth Chaturvedi"
        flavorText = "In Embraal, inventor gangs defend their turf with ingenious devices and blunt objects."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f90f877-4033-4892-a6e7-22d2b393c65d.jpg?1783937209"
    }
}
