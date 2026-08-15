package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

/**
 * Genghis Frog
 * {G}{U}
 * Legendary Creature — Frog Mutant Rogue
 * 1/3
 *
 * Trample
 * Whenever Genghis Frog or another Mutant you control enters, create a Mutagen
 * token. (It's an artifact with "{1}, {T}, Sacrifice this token: Put a +1/+1
 * counter on target creature. Activate only as a sorcery.")
 */
val GenghisFrog = card("Genghis Frog") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Frog Mutant Rogue"
    oracleText = "Trample\nWhenever Genghis Frog or another Mutant you control enters, create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"
    power = 1
    toughness = 3

    keywords(Keyword.TRAMPLE)

    // "Genghis Frog or another Mutant you control" — ANY binding so Genghis's own
    // ETB counts too, filtered to Mutants you control.
    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Creature.youControl().withSubtype(Subtype("Mutant")),
                to = Zone.BATTLEFIELD
            ),
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateMutagenToken()
        description = "Whenever Genghis Frog or another Mutant you control enters, create a Mutagen token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "148"
        artist = "Zoltan Boros"
        flavorText = "\"Free our brothers! The human reign of terror around here ends tonight!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7df26085-eedb-4bdd-a60a-aabfbe9c3157.jpg?1771342425"
    }
}
