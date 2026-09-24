package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Identity Echo — Reality Fracture #87
 * {2}{R} · Enchantment
 *
 * {3}{R}: Exile target creature or planeswalker you control. Reveal cards from the top of your
 * library until you reveal a creature or planeswalker card. Put that card onto the battlefield and
 * the rest on the bottom of your library in a random order. Activate only as a sorcery.
 *
 * The exile is the only targeted step, so an ability whose target has become illegal does nothing
 * at all (CR 608.2b) — no reveal. The reveal walk is `gatherUntilMatch`: it stops at the first
 * creature or planeswalker card and hands back both that card and everything revealed on the way,
 * so the bottom pile is `revealed − match`. If the library holds no creature or planeswalker card,
 * nothing enters and every revealed card goes to the bottom.
 */
val IdentityEcho = card("Identity Echo") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "{3}{R}: Exile target creature or planeswalker you control. Reveal cards from the top " +
        "of your library until you reveal a creature or planeswalker card. Put that card onto the " +
        "battlefield and the rest on the bottom of your library in a random order. Activate only as " +
        "a sorcery."

    activatedAbility {
        cost = Costs.Mana("{3}{R}")
        val permanent = target(
            "target creature or planeswalker you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.youControl()))
        )
        effect = Effects.Pipeline(
            descriptionOverride = "Exile target creature or planeswalker you control. Reveal cards " +
                "from the top of your library until you reveal a creature or planeswalker card. Put " +
                "that card onto the battlefield and the rest on the bottom of your library in a " +
                "random order."
        ) {
            run(Effects.Exile(permanent))
            val walk = gatherUntilMatch(filter = GameObjectFilter.CreatureOrPlaneswalker)
            reveal(walk.revealed)
            move(walk.match, CardDestination.ToZone(Zone.BATTLEFIELD))
            toLibraryBottom(exclude(walk.revealed, walk.match), order = CardOrder.Random)
        }
        timing = TimingRule.SorcerySpeed
        description = "{3}{R}: Exile target creature or planeswalker you control. Reveal cards from " +
            "the top of your library until you reveal a creature or planeswalker card. Put that card " +
            "onto the battlefield and the rest on the bottom of your library in a random order. " +
            "Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "87"
        artist = "Aldo Domínguez"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e600b33b-8916-43dd-95d3-d7cbf874933d.jpg?1789385968"
        inBooster = false
    }
}
