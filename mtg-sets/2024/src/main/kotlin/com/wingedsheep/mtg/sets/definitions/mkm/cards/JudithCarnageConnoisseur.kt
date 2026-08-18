package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Judith, Carnage Connoisseur — Murders at Karlov Manor #210
 * {3}{B}{R} · Legendary Creature — Human Shaman · 3/4
 *
 * Whenever you cast an instant or sorcery spell, choose one —
 * • That spell gains deathtouch and lifelink.
 * • Create a 2/2 red Imp creature token with "When this token dies, it deals 2 damage to each
 *   opponent."
 *
 * A cast trigger with two payoffs that want opposite kinds of spell: mode one turns a burn spell
 * into removal-plus-drain, mode two banks value off a spell that was never going to deal damage
 * (a counterspell, a tutor) — so the choice is made per cast, not per Judith.
 *
 * Modelled as a [ModalEffect] hanging off [Triggers.YouCastInstantOrSorcery]. The engine picks the
 * mode when the *ability resolves* rather than when it's put on the stack (CR 601.2b via 603.3d);
 * that is the existing convention for every modal triggered ability in the corpus (Faces of the
 * Past), and it is unobservable here because the ability resolves before the spell that triggered
 * it either way.
 *
 * Mode one is two [Effects.GrantKeywordToSpell] grants onto [EffectTarget.TriggeringEntity] — the
 * spell object still on the stack, not a permanent. Static keyword projection only reaches
 * battlefield permanents, so both keywords have to be read back off the spell-grant channel when
 * the spell deals its damage; the lifelink half of that read already existed, the deathtouch half
 * is added in this change (`DamageUtils.sourceHasDeathtouch`). Per the rulings, the spell must
 * *itself* deal the damage: a spell that tells another object to deal damage keeps neither keyword's
 * benefit, which falls out for free because the damage source is then that other object.
 *
 * Mode two's Imp carries its death trigger as a token-level [TriggeredAbility] rather than a
 * granted static, so the token is self-contained and a copy of it keeps the trigger.
 */
val JudithCarnageConnoisseur = card("Judith, Carnage Connoisseur") {
    manaCost = "{3}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human Shaman"
    power = 3
    toughness = 4
    oracleText = "Whenever you cast an instant or sorcery spell, choose one —\n" +
        "• That spell gains deathtouch and lifelink.\n" +
        "• Create a 2/2 red Imp creature token with \"When this token dies, it deals 2 damage to " +
        "each opponent.\""

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.Composite(
                    Effects.GrantKeywordToSpell(Keyword.DEATHTOUCH, EffectTarget.TriggeringEntity),
                    Effects.GrantKeywordToSpell(Keyword.LIFELINK, EffectTarget.TriggeringEntity)
                ),
                "That spell gains deathtouch and lifelink"
            ),
            Mode.noTarget(
                CreateTokenEffect(
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.RED),
                    creatureTypes = setOf("Imp"),
                    imageUri = "https://cards.scryfall.io/normal/front/4/7/47a1385b-2be2-49a8-8400-186cd5525dad.jpg?1783912609",
                    triggeredAbilities = listOf(
                        TriggeredAbility.create(
                            trigger = Triggers.Dies.event,
                            binding = Triggers.Dies.binding,
                            effect = DealDamageEffect(2, EffectTarget.PlayerRef(Player.EachOpponent))
                        )
                    )
                ),
                "Create a 2/2 red Imp creature token with \"When this token dies, it deals 2 " +
                    "damage to each opponent.\""
            )
        )
        description = "Whenever you cast an instant or sorcery spell, choose one — " +
            "that spell gains deathtouch and lifelink; or create a 2/2 red Imp creature token " +
            "with \"When this token dies, it deals 2 damage to each opponent.\""
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "210"
        artist = "Jodie Muir"
        flavorText = "\"I don't make my living hiding the truth, detective. I shout it from the stage.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3eaa19ce-cace-499e-8b23-ef9e56b23700.jpg?1783912849"

        ruling(
            "2024-02-02",
            "Judith, Carnage Connoisseur's triggered ability resolves before the spell that " +
                "caused it to trigger. The ability will resolve even if that spell is countered."
        )
        ruling(
            "2024-02-02",
            "An instant or sorcery spell with deathtouch must actually deal damage to a creature " +
                "for it to be destroyed. If the spell instructs another object to deal damage " +
                "(for example, Hard-Hitting Question), the spell doesn't deal any damage itself " +
                "and its instance of deathtouch doesn't apply. Dealing 0 damage isn't dealing damage."
        )
        ruling(
            "2024-02-02",
            "Similarly, an instant or sorcery spell with lifelink must actually deal damage in " +
                "order to cause its controller to gain life. If the spell instructs another " +
                "object to deal damage, its controller won't gain life when that damage is dealt."
        )
    }
}
