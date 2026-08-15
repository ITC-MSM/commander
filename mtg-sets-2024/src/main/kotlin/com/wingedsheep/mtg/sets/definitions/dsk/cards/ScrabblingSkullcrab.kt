package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Scrabbling Skullcrab
 * {U}
 * Creature — Crab Skeleton
 * 0/3
 *
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 * target player mills two cards.
 *
 * Eerie is modeled as two triggered abilities (CR — the keyword ability word covers both an
 * enchantment-you-control entering and fully unlocking a Room). Each independently picks a
 * target player and mills two via [Patterns.Library.mill].
 */
val ScrabblingSkullcrab = card("Scrabbling Skullcrab") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab Skeleton"
    power = 0
    toughness = 3
    oracleText = "Eerie — Whenever an enchantment you control enters and whenever you fully " +
        "unlock a Room, target player mills two cards. (They put the top two cards of their " +
        "library into their graveyard.)"

    keywords(Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        val target = target("target", Targets.Player)
        effect = Patterns.Library.mill(2, target)
        description = "Eerie — Whenever an enchantment you control enters, target player mills two cards."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        val target = target("target", Targets.Player)
        effect = Patterns.Library.mill(2, target)
        description = "Eerie — Whenever you fully unlock a Room, target player mills two cards."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "John Tedrick"
        flavorText = "With sandy beaches in short supply, Duskmourn's crabs have adapted to " +
            "burrow into everything from carpet to flesh."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1017b8e-e7fa-41de-8eb2-2e4a59db5117.jpg?1726286116"
    }
}
