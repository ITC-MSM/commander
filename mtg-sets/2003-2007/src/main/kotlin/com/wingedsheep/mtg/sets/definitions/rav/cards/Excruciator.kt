package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DamageCantBePrevented
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Excruciator
 * {6}{R}{R}
 * Creature — Avatar
 * 7/7
 *
 * Damage that would be dealt by this creature can't be prevented.
 *
 * The same [DamageCantBePrevented] replacement as Leyline of Punishment, *scoped to its own
 * source*: `EventPattern.DamageEvent(source = SourceFilter.Self)` is the "by this creature" half,
 * so every other source's damage on the battlefield stays preventable. Protection prevents damage
 * (CR 702.16e), which is why Excruciator can damage a creature with protection from red — while
 * still being unable to *block* one, since protection's other three letters are untouched.
 *
 * Only effects that use the word "prevent" are switched off: redirection still applies, shield
 * counters and prevention shields survive un-consumed, regeneration still works, and a replacement
 * that swaps the damage for something else (Phytohydra) still replaces it.
 */
val Excruciator = card("Excruciator") {
    manaCost = "{6}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Avatar"
    power = 7
    toughness = 7
    oracleText = "Damage that would be dealt by this creature can't be prevented."

    replacementEffect(
        DamageCantBePrevented(appliesTo = EventPattern.DamageEvent(source = SourceFilter.Self))
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Paolo Parente"
        flavorText = "\"Though used as a piercing weapon, the tusk is more akin to a stinger, " +
            "spreading pain instantly throughout the body of its victim. This specimen deserves " +
            "further study.\"\n—Simic research notes"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3c98460-e8b8-45d6-9bf0-7600b234964b.jpg?1783943655"
        ruling("2005-10-01", "An effect may redirect Excruciator's damage.")
        ruling(
            "2005-10-01",
            "Excruciator can deal damage to creatures with protection from red. (It can't block " +
                "creatures with protection from red, though.)"
        )
        ruling(
            "2005-10-01",
            "Damage prevention shields that would prevent this damage aren't used up and they stick " +
                "around for the next time something would deal damage."
        )
        ruling("2005-10-01", "A creature dealt lethal damage by Excruciator can be regenerated.")
        ruling(
            "2005-10-01",
            "Replacement effects that don't use the word \"prevent\" can replace Excruciator's damage " +
                "with something else. See Phytohydra and Szadek, Lord of Secrets, for example."
        )
    }
}
