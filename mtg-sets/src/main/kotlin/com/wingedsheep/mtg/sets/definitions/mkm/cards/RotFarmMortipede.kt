package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rot Farm Mortipede
 * {3}{B}
 * Creature — Insect
 * 3/4
 *
 * Whenever one or more creature cards leave your graveyard, this creature gets +1/+0
 * and gains menace and lifelink until end of turn.
 */
val RotFarmMortipede = card("Rot Farm Mortipede") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 3
    toughness = 4
    oracleText = "Whenever one or more creature cards leave your graveyard, this creature gets +1/+0 " +
        "and gains menace and lifelink until end of turn."

    // Batching trigger: fires once per event batch no matter how many creature cards left,
    // and regardless of where they went (cast, reanimated, exiled, returned to hand).
    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard(GameObjectFilter.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Loïc Canavaggia"
        flavorText = "Every week, the necropsy lab's unclaimed bodies are transferred to the Golgari for \"recycling.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/2/023b0142-663a-47e7-a9f1-0b565a172b60.jpg?1783912891"
    }
}
