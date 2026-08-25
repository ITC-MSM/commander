package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Syndicate Guildmage — Ravnica Allegiance #211
 * {W}{B} · Creature — Human Cleric · 2 / 2
 *
 * The Orzhov entry in the RNA Guildmage cycle. The tapper is restricted to power 4 or
 * greater, so it needs a filtered [TargetCreature] rather than the shared [Targets.Creature].
 */
val SyndicateGuildmage = card("Syndicate Guildmage") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "{1}{W}, {T}: Tap target creature with power 4 or greater.\n" +
        "{4}{B}, {T}: This creature deals 2 damage to target opponent or planeswalker."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val big = target("target", TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(4))))
        effect = Effects.Tap(big)
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{B}"), Costs.Tap)
        val victim = target("target", Targets.OpponentOrPlaneswalker)
        effect = Effects.DealDamage(2, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Josh Hass"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e82d3c8d-849a-445b-bc7c-365514d1511f.jpg"
    }
}
