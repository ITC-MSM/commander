package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Polluted Dead — Avacyn Restored #116
 * {4}{B} · Creature — Zombie · 3/3
 *
 * When this creature dies, destroy target land.
 *
 * [Triggers.Dies] is the battlefield → graveyard zone change with the default `SELF` binding; the
 * trigger stays indexed on the battlefield (its `triggerZone` is untouched) and reads its
 * last-known information off the zone-change event.
 */
val PollutedDead = card("Polluted Dead") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 3
    oracleText = "When this creature dies, destroy target land."

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target("target", Targets.Land)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Jason A. Engle"
        flavorText = "After the zombie attack, crops withered on the vine, and the thriving village became a ghost town almost overnight."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/036c1954-37d3-4787-8df8-f2d0dd39058a.jpg?1783940692"
    }
}
