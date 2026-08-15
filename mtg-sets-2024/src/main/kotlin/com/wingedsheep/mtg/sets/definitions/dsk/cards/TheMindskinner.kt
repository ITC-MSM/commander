package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ReplaceDamageWithMill
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * The Mindskinner (DSK 66)
 * {U}{U}{U}
 * Legendary Enchantment Creature — Nightmare
 * 10/1
 *
 * The Mindskinner can't be blocked.
 * If a source you control would deal damage to an opponent, prevent that damage and each opponent
 * mills that many cards.
 *
 * The second ability is a damage replacement ([ReplaceDamageWithMill]): it covers both combat
 * damage from the unblockable 10/1 and noncombat damage from any other source you control. The
 * damage is replaced (CR 615) — neither dealt nor "prevented" in the shield sense — and each
 * opponent mills that many cards instead.
 */
val TheMindskinner = card("The Mindskinner") {
    manaCost = "{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Enchantment Creature — Nightmare"
    power = 10
    toughness = 1
    oracleText = "The Mindskinner can't be blocked.\n" +
        "If a source you control would deal damage to an opponent, prevent that damage and each " +
        "opponent mills that many cards."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    replacementEffect(
        ReplaceDamageWithMill(
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Opponent,
                source = SourceFilter.Matching(GameObjectFilter.Any.youControl()),
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "66"
        artist = "Abz J Harding"
        flavorText = "It slices away the most precious memories first, then the mundane, until all that remains is blank, formless fear."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f1bb4c5-99be-46cc-ad54-7affb5f0144c.jpg?1726286101"
    }
}
