package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Invisible Woman, Sue Storm — Marvel Super Heroes #17 (uncommon)
 * {4}{W} · Legendary Creature — Human Hero · 2/5
 *
 * Lifelink
 * Whenever you put one or more +1/+1 counters on one or more other Heroes you control, you may
 * create a 0/4 colorless Wall creature token with defender.
 *
 * Implementation notes:
 * - The headline is the **batch** shape of the counter-placement trigger:
 *   `Triggers.countersPlacedOn(..., batch = true)` (CR 603.2c). One effect that puts a counter on
 *   three of your Heroes is a single occurrence of the trigger event, so it makes one Wall, not
 *   three — the over-count the per-permanent template would produce. Two *separate* effects placing
 *   counters in the same turn are two events and make two Walls.
 * - Each clause of the printed text maps to one axis of the trigger:
 *   - "**you** put" → `placedBy = Player.You` (CR 122.6, and 122.6a for a permanent entering with
 *     counters): an opponent's counters don't fire it.
 *   - "+1/+1 counters" → `counterType = Counters.PLUS_ONE_PLUS_ONE`.
 *   - "**other** Heroes you control" → [TriggerBinding.OTHER] (excludes counters landing on Invisible
 *     Woman herself, who is a Hero) plus a `Creature.youControl().withSubtype(HERO)` filter.
 *   - "one or more" on both nouns → `batch = true`.
 *   - no "first time this turn" rider → `firstTimeEachTurn = false`.
 * - "you **may** create" is `optional = true` on the ability, so the controller gets a yes/no
 *   decision when it resolves rather than the token being forced.
 */
val InvisibleWomanSueStorm = card("Invisible Woman, Sue Storm") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Hero"
    power = 2
    toughness = 5
    oracleText = "Lifelink\n" +
        "Whenever you put one or more +1/+1 counters on one or more other Heroes you control, " +
        "you may create a 0/4 colorless Wall creature token with defender."

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature.youControl().withSubtype(Subtype.HERO),
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            firstTimeEachTurn = false,
            binding = TriggerBinding.OTHER,
            placedBy = Player.You,
            batch = true,
        )
        optional = true
        effect = Effects.CreateToken(
            power = 0,
            toughness = 4,
            creatureTypes = setOf(Subtype.WALL.value),
            keywords = setOf(Keyword.DEFENDER),
            imageUri = "https://cards.scryfall.io/normal/front/8/2/82d35a61-3c87-405d-b857-cf43067cb1c4.jpg?1783902804",
        )
        description = "Whenever you put one or more +1/+1 counters on one or more other Heroes you " +
            "control, you may create a 0/4 colorless Wall creature token with defender."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Paolo Rivera"
        flavorText = "\"I'll hold down the fort while you guys bicker.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f80394b-2f7e-40a7-8203-720bcf39d71b.jpg?1783902974"
    }
}
