package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Vindictive Mob
 * {4}{B}{B}
 * Creature — Human Berserker
 * 5/5
 * When this creature enters, sacrifice a creature.
 * This creature can't be blocked by Saprolings.
 *
 * The bare imperative "sacrifice a creature" is [Effects.SacrificeOwn] — the ability's controller
 * sacrifices, with no player named. `Effects.Sacrifice` is the other sentence ("target opponent
 * sacrifices ..."). The Mob itself is a legal choice, which is what makes it a real drawback when
 * it is the only creature you control.
 *
 * The blocker noun is the bare tribal "Saprolings", i.e. Saproling *permanents* —
 * [GameObjectFilter.Permanent] with the subtype, not `.Creature`.
 */
val VindictiveMob = card("Vindictive Mob") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Berserker"
    oracleText = "When this creature enters, sacrifice a creature.\n" +
        "This creature can't be blocked by Saprolings."
    power = 5
    toughness = 5

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
    }

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Permanent.withSubtype(Subtype.SAPROLING))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "112"
        artist = "Wayne Reynolds"
        flavorText = "Many minds, a single madness."
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23c5c72c-982c-4cfd-b576-089200b4cc04.jpg"
    }
}
