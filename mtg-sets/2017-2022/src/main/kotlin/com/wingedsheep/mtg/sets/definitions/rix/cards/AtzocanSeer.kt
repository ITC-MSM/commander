package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Atzocan Seer
 * {1}{G}{W}
 * Creature — Human Druid
 * 2/3
 * {T}: Add one mana of any color.
 * Sacrifice this creature: Return target Dinosaur card from your graveyard to your hand.
 *
 * The second ability carries no {T} symbol, so it stays activatable while the creature is
 * already tapped from the first.
 */
val AtzocanSeer = card("Atzocan Seer") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Druid"
    oracleText = "{T}: Add one mana of any color.\n" +
        "Sacrifice this creature: Return target Dinosaur card from your graveyard to your hand."
    power = 2
    toughness = 3

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.SacrificeSelf
        val dinosaur = target(
            "target Dinosaur card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Any.withSubtype(Subtype.DINOSAUR).ownedByYou(),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.ReturnToHand(dinosaur)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "153"
        artist = "Joseph Meehan"
        flavorText = "Streams of gold and bright feathers flash in the orb, visions of past and future."
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe84b3c0-bca2-42d3-a82c-540644e59625.jpg?1783935278"
        ruling(
            "2018-01-19",
            "Atzocan Seer's second ability doesn't include the {T} symbol. You can activate that " +
                "ability even if it's already been tapped, perhaps because you activated its " +
                "first ability."
        )
        ruling(
            "2018-01-19",
            "If an effect refers to a \"[subtype] spell\" or \"[subtype] card,\" it refers only " +
                "to a spell or card that has that subtype. For example, March of the Drowned is " +
                "a card that benefits Pirates and features Pirates in its illustration, but it " +
                "isn't a Pirate card."
        )
    }
}
