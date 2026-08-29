package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Vine Snare
 * {2}{G}
 * Instant
 *
 * Prevent all combat damage that would be dealt this turn by creatures with power 4 or less.
 *
 * A source-side shield: [Effects.PreventCombatDamageFrom] installs the prevention keyed on the
 * damage *source* group, so it catches every qualifying creature on both sides of combat rather
 * than protecting one recipient.
 */
val VineSnare = card("Vine Snare") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn by creatures with power 4 or less."

    spell {
        effect = Effects.PreventCombatDamageFrom(
            GroupFilter(GameObjectFilter.Creature.powerAtMost(4))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "205"
        artist = "Igor Kieryluk"
        flavorText = "Nissa found that the vines of the marsh could ensnare just as well as forest vines could—maybe even better."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2241f71-4319-49bf-905a-b6b774ffcb27.jpg"
    }
}
