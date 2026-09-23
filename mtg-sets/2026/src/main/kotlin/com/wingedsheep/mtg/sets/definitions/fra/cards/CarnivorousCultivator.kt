package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val CarnivorousCultivator = card("Carnivorous Cultivator") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warlock"
    power = 2
    toughness = 3
    oracleText = "Deathtouch\nThis creature enters prepared.\n" +
        "Whenever this creature deals combat damage to a player, return target land card from your graveyard to your hand."

    keywords(Keyword.DEATHTOUCH)
    keywords(Keyword.PREPARED)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val land = target(
            "target land card from your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.Move(land, Zone.HAND)
        description = "Whenever this creature deals combat damage to a player, return target land card from your graveyard to your hand."
    }

    prepare("Enroot") {
        manaCost = "{G}"
        typeLine = "Sorcery"
        oracleText = "Search your library for a land card, put it into your graveyard, then shuffle."
        spell {
            effect = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Land,
                destination = SearchDestination.GRAVEYARD,
                shuffleAfter = true,
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "100"
        artist = "Martina Fačková"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79dd5c54-5ea5-47b5-8f9b-50ed57a5ea45.jpg?1789385967"
        inBooster = false
    }
}
