package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blinding Fog
 * {2}{G}
 * Instant
 *
 * Prevent all damage that would be dealt to creatures this turn. Creatures you control gain
 * hexproof until end of turn.
 */
val BlindingFog = card("Blinding Fog") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all damage that would be dealt to creatures this turn. Creatures you " +
        "control gain hexproof until end of turn. (They can't be the targets of spells or " +
        "abilities your opponents control.)"

    spell {
        effect = Effects.PreventAllDamageToGroup(GroupFilter(GameObjectFilter.Creature)) then
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.GrantKeyword(Keyword.HEXPROOF, EffectTarget.Self)
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Igor Kieryluk"
        flavorText = "\"I see you, shiny soldiers, but you won't see me.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bae7d501-72a1-43c2-9f72-d768ac5e9320.jpg"
    }
}
