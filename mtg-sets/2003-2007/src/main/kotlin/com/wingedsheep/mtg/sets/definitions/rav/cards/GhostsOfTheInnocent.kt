package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.HalveDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Ghosts of the Innocent
 * {5}{W}{W}
 * Creature — Spirit
 * 4/5
 *
 * If a source would deal damage to a permanent or player, it deals half that damage, rounded
 * down, to that permanent or player instead.
 *
 * The dividing mirror of Furnace of Rath, and the reason it needs its own [HalveDamage]
 * replacement rather than a `ModifyDamageAmount`: the reduction is *multiplicative*, scaling with
 * the incoming amount, which no `DynamicAmount` can read. Everything else falls out of that —
 * half of 1 rounded down is 0 (a 1-damage source deals nothing), and three copies compound
 * 14 → 7 → 3 → 1, because each applicable replacement applies once (CR 616.1).
 *
 * `RecipientFilter.Any` is the "a permanent **or** player" half: unlike most of the damage family
 * this card scopes neither by recipient nor by source, so every damage event in the game is
 * halved — including damage dealt to the Ghosts themselves and to their controller.
 *
 * It is emphatically **not** a prevention effect, which is what makes it the one thing on the
 * battlefield that still shrinks Excruciator's damage (also in this set).
 */
val GhostsOfTheInnocent = card("Ghosts of the Innocent") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 4
    toughness = 5
    oracleText = "If a source would deal damage to a permanent or player, it deals half that " +
        "damage, rounded down, to that permanent or player instead."

    replacementEffect(
        HalveDamage(appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.Any))
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Kev Walker"
        flavorText = "\"Ma said we should offer up blini-cakes and salt to the good ones, but I " +
            "get that chill up my spine and just shut the door.\"\n—Otak, Tin Street shopkeep"
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5b10c5f-261f-4301-b675-d1f52b859360.jpg?1783943699"
        ruling(
            "2005-10-01",
            "If a damage prevention effect and Ghosts of the Innocent's effect would apply to the " +
                "same damage, the player or the controller of the creature being dealt damage may " +
                "apply the effects in either order (most likely applying the halving effect first)."
        )
        ruling(
            "2005-10-01",
            "Half of 1 rounded down is 0. A source that would deal 1 damage won't deal damage at all."
        )
        ruling(
            "2005-10-01",
            "Multiple Ghosts of the Innocent effects are cumulative, and each will halve the " +
                "damage, rounded down. For example, with three on the battlefield, 14 damage " +
                "becomes 7, then 3, then finally 1, and only 1 damage would actually be dealt."
        )
        ruling(
            "2005-10-01",
            "This isn't a damage prevention effect. If Excruciator (\"Damage that would be dealt " +
                "by Excruciator can't be prevented\") would deal 7 damage to a permanent or " +
                "player, it deals 3 damage instead."
        )
        ruling("2005-10-01", "If damage is redirected, it's only halved once.")
        ruling(
            "2005-10-01",
            "If both Ghosts of the Innocent and Furnace of Rath (which doubles damage) are on the " +
                "battlefield, the controller of the permanent being dealt damage or the player " +
                "being dealt damage can apply the effects in either order. This can matter if the " +
                "original amount of damage is odd."
        )
    }
}
