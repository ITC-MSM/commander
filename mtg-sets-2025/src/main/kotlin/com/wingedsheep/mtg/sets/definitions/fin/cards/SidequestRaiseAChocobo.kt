package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sidequest: Raise a Chocobo // Black Chocobo — Final Fantasy #201
 * {1}{G} · Enchantment // Creature — Bird · 2/2
 *
 * Front — Sidequest: Raise a Chocobo:
 *   When this enchantment enters, create a 2/2 green Bird creature token with "Whenever a
 *   land you control enters, this token gets +1/+0 until end of turn."
 *   At the beginning of your first main phase, if you control four or more Birds,
 *   transform this enchantment.
 *
 * Back — Black Chocobo:
 *   When this permanent transforms into Black Chocobo, search your library for a land card,
 *   put it onto the battlefield tapped, then shuffle.
 *   Landfall — Whenever a land you control enters, Birds you control get +1/+0 until end of turn.
 *
 * The token (same one Chocobo Racetrack makes) carries a granted landfall trigger via
 * [CreateTokenEffect.triggeredAbilities]. The first-main transform is an intervening-"if"
 * over the number of Birds you control.
 */
private val BlackChocobo = card("Black Chocobo") {
    manaCost = ""
    colorIdentity = "G"
    typeLine = "Creature — Bird"
    oracleText = "When this permanent transforms into Black Chocobo, search your library for a " +
        "land card, put it onto the battlefield tapped, then shuffle.\n" +
        "Landfall — Whenever a land you control enters, Birds you control get +1/+0 until end of turn."
    power = 2
    toughness = 2

    // When this permanent transforms into Black Chocobo, search your library for a land card,
    // put it onto the battlefield tapped, then shuffle.
    triggeredAbility {
        trigger = Triggers.TransformsToBack
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Land,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
        )
    }

    // Landfall — Whenever a land you control enters, Birds you control get +1/+0 until end of turn.
    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Patterns.Group.modifyStatsForAll(
            power = 1,
            toughness = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Bird").youControl()),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "201"
        artist = "Sansyu"
        imageUri = "https://cards.scryfall.io/normal/back/0/c/0cbf911c-a721-4b84-8645-d83a0966be18.jpg?1782686447"
    }
}

private val SidequestRaiseAChocoboFront = card("Sidequest: Raise a Chocobo") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create a 2/2 green Bird creature token with " +
        "\"Whenever a land you control enters, this token gets +1/+0 until end of turn.\"\n" +
        "At the beginning of your first main phase, if you control four or more Birds, transform this enchantment."

    // When this enchantment enters, create a 2/2 green Bird creature token with
    // "Whenever a land you control enters, this token gets +1/+0 until end of turn."
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Bird"),
            imageUri = "https://cards.scryfall.io/normal/front/1/f/1fbc471d-5948-47fc-b7cc-81cc13a4cd15.jpg?1782725374",
            triggeredAbilities = listOf(
                TriggeredAbility.create(
                    trigger = Triggers.entersBattlefield(
                        filter = GameObjectFilter.Land.youControl(),
                        binding = TriggerBinding.ANY,
                    ).event,
                    binding = TriggerBinding.ANY,
                    effect = Effects.ModifyStats(1, 0, EffectTarget.Self),
                ),
            ),
        )
    }

    // At the beginning of your first main phase, if you control four or more Birds,
    // transform this enchantment.
    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        triggerCondition = Conditions.YouControlAtLeast(4, GameObjectFilter.Creature.withSubtype("Bird"))
        effect = TransformEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "201"
        artist = "Miho Midorikawa"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0cbf911c-a721-4b84-8645-d83a0966be18.jpg?1782686447"

        ruling(
            "2025-06-06",
            "Sidequest: Raise a Chocobo's last ability checks at the moment it would trigger to " +
                "see if you control four or more Birds. If you don't, the ability won't trigger " +
                "at all. If it does trigger, the ability will check again as it tries to resolve."
        )
        ruling(
            "2025-06-06",
            "If this card somehow enters the battlefield with its back face up, it didn't " +
                "transform, so you won't search."
        )
    }
}

val SidequestRaiseAChocobo: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = SidequestRaiseAChocoboFront,
    backFace = BlackChocobo,
)
