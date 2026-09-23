package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Mabel, Bitter Recluse — "remove up to three counters" is [Effects.RemoveCountersUpTo]: the
 * controller picks how many of each kind to take off, capped at three in total (Heartless Act's
 * shape). "Another target" excludes Mabel herself; the target may be any player's permanent.
 */
val MabelBitterRecluse = card("Mabel, Bitter Recluse") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Mouse Warlock"
    power = 1
    toughness = 1
    oracleText = "Deathtouch\n" +
        "When Mabel enters, remove up to three counters from another target creature or planeswalker."

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target(
            "another target creature or planeswalker",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker).other()),
        )
        effect = Effects.RemoveCountersUpTo(3, victim)
        description = "When Mabel enters, remove up to three counters from another target creature or planeswalker."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Matt Stewart"
        flavorText = "With her village destroyed, Mabel turned to the Calamity Beasts to bring down King Glarb."
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2a412b0-2ae4-4552-bc5e-70654b6b9b4e.jpg?1789385677"
        inBooster = false
    }
}
