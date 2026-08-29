package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DonorCards
import com.wingedsheep.sdk.scripting.HasAllActivatedAbilitiesOfCards
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Patchwork Crawler
 * {1}{U}
 * Creature — Zombie Horror
 * 1/2
 *
 * {2}{U}: Exile target creature card from your graveyard and put a +1/+1 counter on this creature.
 * This creature has all activated abilities of all creature cards exiled with it.
 *
 * The Territory Forge shape: the activated ability files each exiled card in this creature's
 * linked-exile pile ([Effects.ExileLinkedToSource]), and [HasAllActivatedAbilitiesOfCards] over
 * `LINKED_EXILE` surfaces every activated ability of that pile on the Crawler itself. Only creature
 * cards can be exiled this way, so the pile never holds anything the static shouldn't read. Per the
 * rulings the grant is activated abilities only, and it names the Crawler in place of the printed
 * card — both are what the static already does.
 */
val PatchworkCrawler = card("Patchwork Crawler") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Horror"
    power = 1
    toughness = 2
    oracleText = "{2}{U}: Exile target creature card from your graveyard and put a +1/+1 counter " +
        "on this creature.\n" +
        "This creature has all activated abilities of all creature cards exiled with it."

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val exiled = target("target creature card from your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = Effects.Composite(
            Effects.ExileLinkedToSource(exiled),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "Exile target creature card from your graveyard and put a +1/+1 counter " +
            "on Patchwork Crawler."
    }

    staticAbility {
        ability = HasAllActivatedAbilitiesOfCards(donors = DonorCards.LINKED_EXILE)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "72"
        artist = "Fesbra"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bb86ebf-f145-43c1-8f05-f4b55ded59b6.jpg?1783924886"
        ruling(
            "2021-11-19",
            "Patchwork Crawler gains only activated abilities. It doesn't gain triggered abilities " +
                "or static abilities."
        )
        ruling(
            "2021-11-19",
            "If an activated ability of a card in exile references the card it's printed on by " +
                "name, treat Patchwork Crawler's version of that ability as though it referenced " +
                "Patchwork Crawler by name instead."
        )
        ruling(
            "2021-11-19",
            "Once Patchwork Crawler leaves the battlefield, it will no longer have the activated " +
                "abilities of the creature cards exiled with it."
        )
    }
}
