package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Kami of the Palace Fields — Champions of Kamigawa #24.
 *
 * Soulshift is composed from the normal dies-trigger, targeted graveyard-card,
 * optional-resolution, and zone-move primitives.  The target is deliberately
 * `Any.withSubtype(SPIRIT)`, rather than Creature: Soulshift says “Spirit card”,
 * which also permits a noncreature card with the Spirit subtype.
 */
val KamiOfThePalaceFields = card("Kami of the Palace Fields") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Flying, first strike\nSoulshift 5 (When this creature dies, you may return target Spirit card with mana value 5 or less from your graveyard to your hand.)"
    power = 3
    toughness = 3
    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)
    triggeredAbility {
        trigger = Triggers.Dies
        val spirit = target(
            "target Spirit card with mana value 5 or less from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Any.withSubtype(Subtype.SPIRIT).ownedByYou().manaValueAtMost(5),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        // The "may" is intentionally an effect gate, not `optional = true`.
        // The required target is chosen while the trigger is put on the stack;
        // `MayEffect` asks yes/no only when that targeted stack object resolves.
        effect = MayEffect(Effects.Move(spirit, Zone.HAND, fromZone = Zone.GRAVEYARD))
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/690980ce-bbdc-4d52-b34e-2bad11e436a1.jpg?1783944337"
    }
}
