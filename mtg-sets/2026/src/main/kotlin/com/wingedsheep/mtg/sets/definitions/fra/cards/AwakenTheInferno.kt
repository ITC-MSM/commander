package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

val AwakenTheInferno = card("Awaken the Inferno") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Awaken the Inferno deals 6 damage to target creature or planeswalker an opponent controls. " +
        "Put a +1/+1 counter on up to one target creature you control.\n" +
        "Basic landcycling {2} ({2}, Discard this card: Search your library for a basic land card, " +
        "reveal it, put it into your hand, then shuffle.)"

    spell {
        val victim = target(
            "creature or planeswalker an opponent controls",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls())),
        )
        // The optional target stays last so declining it can't shift the required one's index.
        val ally = target(
            "up to one target creature you control",
            TargetCreature(filter = TargetFilter.Creature.youControl(), optional = true),
        )
        effect = Effects.DealDamage(6, victim) then
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, ally)
    }

    keywordAbility(KeywordAbility.basicLandcycling(ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Kai Carpenter"
        flavorText = "Nothing burns like betrayal."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c596c4ec-8480-4be9-a45d-700398a126f6.jpg?1789556812"
        inBooster = false
    }
}
