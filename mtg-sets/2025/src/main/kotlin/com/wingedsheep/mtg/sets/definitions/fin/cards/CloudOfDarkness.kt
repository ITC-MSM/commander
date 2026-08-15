package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount


/**
 * Cloud of Darkness
 * {2}{B}{G}{G}
 * Legendary Creature — Avatar
 * 3/3
 * Flying
 * Particle Beam — When Cloud of Darkness enters, target creature an opponent controls gets -X/-X until end of turn, where X is the number of permanent cards in your graveyard.
 */
val CloudOfDarkness = card("Cloud of Darkness") {
    manaCost = "{2}{B}{G}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Avatar"
    oracleText = "Flying\nParticle Beam — When Cloud of Darkness enters, target creature an opponent controls gets -X/-X until end of turn, where X is the number of permanent cards in your graveyard."
    power = 3
    toughness = 3
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.opponentControls()))
        // X = the number of permanent cards in your graveyard.
        effect = Effects.ModifyStats(
            DynamicAmount.Multiply(DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Permanent), -1),
            DynamicAmount.Multiply(DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Permanent), -1),
            t
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Fariba Khamseh"
        flavorText = "\"We are the Cloud of Darkness! Bringer of wrath and destroyer of light!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a292094a-674a-401f-8776-ba5ebe57c946.jpg"
    }
}
