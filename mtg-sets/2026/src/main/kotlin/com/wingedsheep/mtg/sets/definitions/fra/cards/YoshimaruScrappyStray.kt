package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val YoshimaruScrappyStray = card("Yoshimaru, Scrappy Stray") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Dog"
    power = 1
    toughness = 1
    oracleText = "When Yoshimaru enters, another target creature you control fights up to one target creature " +
        "an opponent controls. (Each deals damage equal to its power to the other.)\n" +
        "{6}: Put a +1/+1 counter on target nonlegendary creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val ally = target(
            "another target creature you control",
            TargetCreature(filter = TargetFilter.OtherCreatureYouControl),
        )
        val foe = target(
            "up to one target creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls),
        )
        effect = Effects.Fight(ally, foe)
        description = "When Yoshimaru enters, another target creature you control fights up to one target " +
            "creature an opponent controls."
    }

    activatedAbility {
        cost = Costs.Mana("{6}")
        val creature = target(
            "target nonlegendary creature",
            TargetCreature(filter = TargetFilter.NonlegendaryCreature),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "269"
        artist = "Brian Valeza"
        flavorText = "His cunning leadership of Towashi's dogs earned him the nickname \"The Stray Emperor.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8dfd087-2434-42c6-ac4c-1decbcdde2db.jpg?1789470909"
        inBooster = false
    }
}
