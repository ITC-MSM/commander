package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Krang, Utrom Warlord
 * {9}
 * Legendary Artifact Creature — Utrom Robot
 * 9/9
 *
 * Flying, trample, indestructible, haste
 * Other artifact creatures you control have flying, trample,
 * indestructible, and haste.
 */
val KrangUtromWarlord = card("Krang, Utrom Warlord") {
    manaCost = "{9}"
    typeLine = "Legendary Artifact Creature — Utrom Robot"
    oracleText = "Flying, trample, indestructible, haste\nOther artifact creatures you control have flying, trample, indestructible, and haste."
    power = 9
    toughness = 9

    keywords(Keyword.FLYING, Keyword.TRAMPLE, Keyword.INDESTRUCTIBLE, Keyword.HASTE)

    val otherArtifactCreatures = GroupFilter(
        GameObjectFilter(
            cardPredicates = listOf(
                CardPredicate.IsCreature,
                CardPredicate.IsArtifact,
            )
        ).youControl(),
        excludeSelf = true
    )

    for (kw in listOf(Keyword.FLYING, Keyword.TRAMPLE, Keyword.INDESTRUCTIBLE, Keyword.HASTE)) {
        staticAbility {
            ability = GrantKeyword(keyword = kw, filter = otherArtifactCreatures)
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "175"
        artist = "Lordigan"
        flavorText = "The Molecular Amplification technology of Krang's exo-suit allows him to expand and adapt not only his tactics, but his physical form."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88d36c32-d6f9-46e4-9cc8-08e6c0ff05d2.jpg?1769006413"
    }
}
