package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Indentured Oaf
 * {3}{R}
 * Creature — Ogre Warrior
 * 4/3
 *
 * Prevent all damage that this creature would deal to red creatures.
 *
 * A continuous [PreventDamage] replacement (CR 615) scoped on both sides: source is the Oaf itself
 * ([SourceFilter.Self]) and the recipient is any red creature. The recipient filter is evaluated
 * against projected state at the moment damage would be dealt, so a creature that has been made red
 * — or has lost red — since blockers were declared is judged on its colour *then*, and the Oaf's own
 * controller is not spared: the sentence says "red creatures", not "red creatures an opponent
 * controls".
 */
val IndenturedOaf = card("Indentured Oaf") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 4
    toughness = 3
    oracleText = "Prevent all damage that this creature would deal to red creatures."

    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Matching(GameObjectFilter.Creature.withColor(Color.RED)),
                source = SourceFilter.Self
            )
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Glenn Fabry"
        flavorText = "All it knows is the difference between friend and food."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af6e976c-3c0b-4ba5-b614-e1b576b57e86.jpg?1783943651"
    }
}
