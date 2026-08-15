package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantMayCastFromLinkedExile

/**
 * Intrepid Paleontologist
 * {1}{G}
 * Creature — Human Druid
 * 2/2
 *
 * {T}: Add one mana of any color.
 * {2}: Exile target card from a graveyard.
 * You may cast Dinosaur creature spells from among cards you own exiled with this creature.
 * If you cast a spell this way, that creature enters with a finality counter on it. (If a
 * creature with a finality counter on it would die, exile it instead.)
 */
val IntrepidPaleontologist = card("Intrepid Paleontologist") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 2
    toughness = 2
    oracleText = "{T}: Add one mana of any color.\n" +
        "{2}: Exile target card from a graveyard.\n" +
        "You may cast Dinosaur creature spells from among cards you own exiled with this creature. " +
        "If you cast a spell this way, that creature enters with a finality counter on it. " +
        "(If a creature with a finality counter on it would die, exile it instead.)"

    // {T}: Add one mana of any color.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    // {2}: Exile target card from a graveyard, linked to this creature so the Dinosaur-cast
    // permission below can find it.
    activatedAbility {
        cost = Costs.Mana("{2}")
        val graveyardTarget = target("target card in a graveyard", Targets.CardInGraveyard)
        effect = Effects.Move(
            target = graveyardTarget,
            destination = Zone.EXILE,
            linkToSource = true
        )
    }

    // You may cast Dinosaur creature spells from among cards you own exiled with this creature.
    // A creature cast this way enters with a finality counter on it (the entersWithCounter rider).
    staticAbility {
        ability = GrantMayCastFromLinkedExile(
            filter = GameObjectFilter.Creature.withSubtype("Dinosaur"),
            ownedByYou = true,
            entersWithCounter = CounterType.FINALITY
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "193"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/871a164a-0fe6-480e-a1be-cbffce884bd3.jpg?1782694454"

        ruling("2023-11-10", "Finality counters aren't a keyword ability. A finality counter doesn't give any abilities to the permanent it's on.")
        ruling("2023-11-10", "You cast the exiled Dinosaur creature spell using the permission Intrepid Paleontologist grants. You must still pay that spell's costs and follow its normal timing permissions (usually sorcery speed).")
        ruling("2023-11-10", "If Intrepid Paleontologist leaves the battlefield, the cards exiled with it remain exiled and can no longer be cast.")
    }
}
