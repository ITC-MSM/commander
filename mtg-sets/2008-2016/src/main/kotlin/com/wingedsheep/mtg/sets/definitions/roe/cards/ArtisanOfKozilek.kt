package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Artisan of Kozilek
 * {9}
 * Creature — Eldrazi
 * 10/9
 *
 * When you cast this spell, you may return target creature card from your graveyard to the
 * battlefield.
 * Annihilator 2
 *
 * Modeling notes:
 *  - The reanimation is a **cast trigger** ([Triggers.WhenYouCastThisSpell]), not an enters
 *    trigger — it resolves before Artisan itself, and it still resolves if Artisan is countered.
 *  - Annihilator is a display-only [KeywordAbility.Numeric] in the SDK, so the behaviour is
 *    lowered here as the triggered ability the keyword abbreviates: on attack, the defending
 *    player sacrifices two permanents of their choice. [Effects.Sacrifice] is the edict form —
 *    the *defending player* chooses — and the filter is [GameObjectFilter.Permanent] rather than
 *    `Creature`, because annihilator eats any permanent.
 */
val ArtisanOfKozilek = card("Artisan of Kozilek") {
    manaCost = "{9}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
    power = 10
    toughness = 9
    oracleText = "When you cast this spell, you may return target creature card from your " +
        "graveyard to the battlefield.\n" +
        "Annihilator 2 (Whenever this creature attacks, defending player sacrifices two " +
        "permanents of their choice.)"

    keywordAbility(KeywordAbility.annihilator(2))

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        val creatureCard = target(
            "target creature card from your graveyard",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard)
        )
        effect = MayEffect(Effects.Move(creatureCard, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD))
        description = "When you cast this spell, you may return target creature card from your " +
            "graveyard to the battlefield."
    }

    // Annihilator 2 — the lowering of the display-only keyword ability above.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Sacrifice(
            GameObjectFilter.Permanent,
            2,
            EffectTarget.PlayerRef(Player.DefendingPlayer)
        )
        description = "Annihilator 2"
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Jason Felix"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3ac80eb8-321d-476a-87e7-d25bdac6a91c.jpg"
    }
}
