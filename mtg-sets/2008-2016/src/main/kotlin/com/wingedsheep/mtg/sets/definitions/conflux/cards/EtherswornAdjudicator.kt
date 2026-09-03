package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ethersworn Adjudicator
 * {4}{U}
 * Artifact Creature — Vedalken Knight
 * 4 / 4
 * Flying
 * {1}{W}{B}, {T}: Destroy target creature or enchantment.
 * {2}{U}: Untap this creature.
 *
 * The removal is [Effects.Destroy] over [Targets.CreatureOrEnchantment] — one requirement whose
 * filter is the printed disjunction, not two abilities. The untap is the untapped half of the
 * same tap/untap atom, [Effects.Untap] on [EffectTarget.Self], and it deliberately carries no
 * tap in its own cost: paying {2}{U} is what lets the Adjudicator destroy again.
 */
val EtherswornAdjudicator = card("Ethersworn Adjudicator") {
    manaCost = "{4}{U}"
    colorIdentity = "BUW"
    typeLine = "Artifact Creature — Vedalken Knight"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "{1}{W}{B}, {T}: Destroy target creature or enchantment.\n" +
        "{2}{U}: Untap this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}{B}"), Costs.Tap)
        val victim = target("target", Targets.CreatureOrEnchantment)
        effect = Effects.Destroy(victim)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "26"
        artist = "Dan Murayama Scott"
        flavorText = "Esper mages devised their weapons to be so devastating that war seemed unnecessary."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a35a056c-1e38-416b-bf01-2a1762b08020.jpg"
    }
}
