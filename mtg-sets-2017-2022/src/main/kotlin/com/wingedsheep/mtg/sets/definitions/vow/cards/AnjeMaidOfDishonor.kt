package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Anje, Maid of Dishonor — Innistrad: Crimson Vow #231
 * {2}{B}{R} · Legendary Creature — Vampire · Rare · 4/5
 * Artist: Yongjae Choi
 *
 * Whenever Anje and/or one or more other Vampires you control enter, create a Blood token. This
 * ability triggers only once each turn.
 * {2}, Sacrifice another creature or a Blood token: Each opponent loses 2 life and you gain 2 life.
 *
 * The trigger is a batching "enters" ability that includes the source ("Anje and/or one or more
 * other Vampires"): [Triggers.OneOrMorePermanentsEnter] over your Vampires with `excludeSource =
 * false`, so Anje's own entry counts. `oncePerTurn = true` implements "This ability triggers only
 * once each turn" — the batching trigger already fires once per simultaneous group, and the flag
 * caps it at one firing across the whole turn (reset each turn). The activated ability's additional
 * cost sacrifices *another* creature or a Blood token ([Costs.SacrificeAnother] excludes the source,
 * over a `Creature or Blood-artifact` filter — the Bloodtithe Harvester Blood-token filter), then
 * drains: each opponent loses 2 life and you gain 2 ([Effects.LoseLife] to [Player.EachOpponent] +
 * [Effects.GainLife]).
 */
val AnjeMaidOfDishonor = card("Anje, Maid of Dishonor") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Vampire"
    power = 4
    toughness = 5
    oracleText = "Whenever Anje and/or one or more other Vampires you control enter, create a Blood " +
        "token. This ability triggers only once each turn. (It's an artifact with \"{1}, {T}, " +
        "Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "{2}, Sacrifice another creature or a Blood token: Each opponent loses 2 life and you gain " +
        "2 life."

    // Whenever Anje and/or one or more other Vampires you control enter, create a Blood token.
    // This ability triggers only once each turn.
    triggeredAbility {
        trigger = Triggers.OneOrMorePermanentsEnter(
            GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl(),
            excludeSource = false
        )
        oncePerTurn = true
        effect = Effects.CreateBlood()
        description = "Whenever Anje and/or one or more other Vampires you control enter, create a " +
            "Blood token. This ability triggers only once each turn."
    }

    // {2}, Sacrifice another creature or a Blood token: Each opponent loses 2 life and you gain 2 life.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.SacrificeAnother(
                GameObjectFilter.Creature or GameObjectFilter.Artifact.withSubtype("Blood")
            )
        )
        effect = Effects.Composite(
            Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(2)
        )
        description = "{2}, Sacrifice another creature or a Blood token: Each opponent loses 2 life " +
            "and you gain 2 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Yongjae Choi"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bfac4ab-97f1-448c-8554-42ed03eb5656.jpg?1783924794"
    }
}
