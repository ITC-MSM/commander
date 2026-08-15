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
 * Dashing Bloodsucker
 * {3}{B}
 * Creature — Vampire Warrior
 * 2/5
 *
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 * this creature gets +2/+0 and gains lifelink until end of turn.
 *
 * Eerie is modeled as two triggered abilities (the keyword ability word covers both an
 * enchantment-you-control entering and fully unlocking a Room). Each pumps the source +2/+0 and
 * grants lifelink until end of turn via [EffectTarget.Self].
 */
val DashingBloodsucker = card("Dashing Bloodsucker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior"
    power = 2
    toughness = 5
    oracleText = "Eerie — Whenever an enchantment you control enters and whenever you fully " +
        "unlock a Room, this creature gets +2/+0 and gains lifelink until end of turn."

    keywords(Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
        )
        description = "Eerie — Whenever an enchantment you control enters, Dashing Bloodsucker " +
            "gets +2/+0 and gains lifelink until end of turn."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
        )
        description = "Eerie — Whenever you fully unlock a Room, Dashing Bloodsucker gets +2/+0 " +
            "and gains lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Randy Gallegos"
        flavorText = "\"There are worse things here than vampires.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/9/790a90cc-d36f-43b5-8423-89e30bdf7b9f.jpg?1726286186"
    }
}
