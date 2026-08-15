package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Crossway Troublemakers
 * {5}{B}
 * Creature — Vampire
 * 5/5
 *
 * Attacking Vampires you control have deathtouch and lifelink.
 * Whenever a Vampire you control dies, you may pay 2 life. If you do, draw a card.
 *
 * The two combat keywords are separate [GrantKeyword] statics over the same filter (attacking
 * Vampires you control — Crossway itself qualifies while attacking). The death trigger mirrors the
 * established "pay N life. If you do, draw" shape (Call of the Ring): a [MayEffect] whose
 * consequence is `LoseLife` + `DrawCards`.
 */
val CrosswayTroublemakers = card("Crossway Troublemakers") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    oracleText = "Attacking Vampires you control have deathtouch and lifelink. (Any amount of " +
        "damage they deal to a creature is enough to destroy it. Damage dealt by those creatures " +
        "also causes their controller to gain that much life.)\n" +
        "Whenever a Vampire you control dies, you may pay 2 life. If you do, draw a card."
    power = 5
    toughness = 5

    val attackingVampiresYouControl =
        GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).attacking().youControl())

    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH, attackingVampiresYouControl)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK, attackingVampiresYouControl)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = MayEffect(
            effect = Effects.Composite(
                listOf(
                    Effects.LoseLife(2, EffectTarget.Controller),
                    Effects.DrawCards(1),
                )
            ),
            descriptionOverride = "You may pay 2 life. If you do, draw a card."
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Aaron J. Riley"
        flavorText = "\"Lady Anje said no violence at the wedding. She never said anything about " +
            "the journey there!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/3/431711c5-c04f-4d34-97c9-5199cfbf9da9.jpg?1783925002"
    }
}
