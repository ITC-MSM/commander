package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cult Healer
 * {2}{W}
 * Creature — Human Doctor
 * 2/3
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 * this creature gains lifelink until end of turn.
 */
val CultHealer = card("Cult Healer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Doctor"
    power = 3
    toughness = 3
    oracleText = "Eerie — Whenever an enchantment you control enters and whenever you fully unlock " +
        "a Room, this creature gains lifelink until end of turn."

    keywords(Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
        description = "Eerie — Whenever an enchantment you control enters, this creature gains lifelink until end of turn."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
        description = "Eerie — Whenever you fully unlock a Room, this creature gains lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Diana Franco"
        flavorText = "\"Oh, we don't need to operate. I've just always wanted to try it.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c9b8fbe-8a5e-4b62-b53f-9ead8147bbbb.jpg?1726285862"
    }
}
