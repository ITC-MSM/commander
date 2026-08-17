package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AnyPlayerMayPayEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The candidate domain of an "any player may [cost]" effect
 * ([com.wingedsheep.engine.handlers.effects.player.AnyPlayerMayPayExecutor], and the next-player
 * path in [com.wingedsheep.engine.handlers.continuations.SacrificeAndPayContinuationResumer]).
 *
 * Two properties, both broken before
 * [issue #1880](https://github.com/wingedsheep/argentum-engine/issues/1880):
 *
 * - the atom's own `excludeSelf` — and nothing else — decides whether the effect's source is a
 *   legal pick. The helper took a `sourceId` and ignored it, so a "sacrifice **another** creature"
 *   cost offered the source and reported a source-only board payable.
 * - both the initial prompt and the continuation read *projected* control. The continuation used a
 *   raw `ZoneKey(player, BATTLEFIELD)` scan, which is keyed by **ownership**, so a stolen creature
 *   was offered to its owner rather than to the player who actually controls it.
 */
class AnyPlayerMayPayCandidateDomainTest : FunSpec({

    /** "When this enters, any player may sacrifice a creature. If a player does, they gain 3 life." */
    val SacrificeAny = card("Test Any-Player Sacrifice") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = AnyPlayerMayPayEffect(
                cost = Costs.pay.Sacrifice(GameObjectFilter.Creature, count = 1),
                consequence = Effects.GainLife(3)
            )
        }
    }

    /** The same, printed "another" — the source is out of the pool. */
    val SacrificeAnother = card("Test Any-Player Sacrifice Another") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = AnyPlayerMayPayEffect(
                cost = Costs.pay.SacrificeAnother(GameObjectFilter.Creature, count = 1),
                consequence = Effects.GainLife(3)
            )
        }
    }

    /**
     * The "another" shape with flash, so the *non-active* player can be the one who controls the
     * source — the only way the continuation's next-player path meets the source at all, since the
     * active player is always asked first.
     */
    val SacrificeAnotherFlash = card("Test Any-Player Sacrifice Another Flash") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        keywords(Keyword.FLASH)
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = AnyPlayerMayPayEffect(
                cost = Costs.pay.SacrificeAnother(GameObjectFilter.Creature, count = 1),
                consequence = Effects.GainLife(3)
            )
        }
    }

    val Bear = card("Test Bear") {
        manaCost = "{0}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    /** {0} sorcery: "Gain control of target creature." Used to split control from ownership. */
    val StealCreature = card("Test Steal Creature") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        spell {
            val t = target("creature", Targets.Creature)
            effect = Effects.GainControl(t, Duration.Permanent)
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCards(listOf(SacrificeAny, SacrificeAnother, SacrificeAnotherFlash, Bear, StealCreature))
        d.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Put [cardName] into [playerId]'s hand, cast it, and let it resolve onto the battlefield. */
    fun GameTestDriver.resolveCreature(playerId: EntityId, cardName: String): EntityId {
        val cardId = putCardInHand(playerId, cardName)
        giveMana(playerId, Color.GREEN, 1)
        castSpell(playerId, cardId)
        bothPass()
        return cardId
    }

    test("excludeSelf=false — the source alone is offered and can pay its own cost") {
        val d = driver()
        val active = d.activePlayer!!
        val source = d.resolveCreature(active, "Test Any-Player Sacrifice")

        d.bothPass() // resolve the ETB trigger

        val decision = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe active
        decision.options shouldContainExactly listOf(source)

        d.submitCardSelection(active, listOf(source))
        d.getGraveyardCardNames(active) shouldContain "Test Any-Player Sacrifice"
        d.getLifeTotal(active) shouldBe 23 // the consequence ran
    }

    test("excludeSelf=true — a board holding only the source is skipped, not prompted") {
        val d = driver()
        val active = d.activePlayer!!
        d.resolveCreature(active, "Test Any-Player Sacrifice Another")

        d.bothPass() // resolve the ETB trigger

        // Neither player can pay: the source is excluded and nobody else has a creature.
        d.pendingDecision shouldBe null
        d.findPermanent(active, "Test Any-Player Sacrifice Another") shouldNotBe null
    }

    test("excludeSelf=true — the source is kept out of the prompt's options") {
        val d = driver()
        val active = d.activePlayer!!
        val bear = d.putCreatureOnBattlefield(active, "Test Bear")
        val source = d.resolveCreature(active, "Test Any-Player Sacrifice Another")

        d.bothPass() // resolve the ETB trigger

        val decision = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe active
        decision.options shouldContainExactly listOf(bear)
        decision.options shouldNotContain source
    }

    test("continuation — the next player's options exclude the source they control") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = if (active == d.player1) d.player2 else d.player1

        // The active player has a creature (so they are asked first and can decline); the opponent
        // flashes in the source, so they control it and are asked second — the continuation path.
        d.putCreatureOnBattlefield(active, "Test Bear")
        val oppBear = d.putCreatureOnBattlefield(opponent, "Test Bear")

        val source = d.putCardInHand(opponent, "Test Any-Player Sacrifice Another Flash")
        d.giveMana(opponent, Color.GREEN, 1)
        d.passPriority(active)
        d.castSpell(opponent, source)
        d.bothPass() // resolve the creature
        d.bothPass() // resolve the ETB trigger

        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>().playerId shouldBe active
        d.submitCardSelection(active, emptyList()) // decline → continuation asks the opponent

        val next = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        next.playerId shouldBe opponent
        next.options shouldContainExactly listOf(oppBear)
        next.options shouldNotContain source
    }

    test("continuation — the next player's options follow projected control, not ownership") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = if (active == d.player1) d.player2 else d.player1

        // The opponent owns a Bear; the active player steals it. Ownership still puts that Bear in
        // the opponent's battlefield zone, so a raw zone scan would offer it back to them.
        val stolen = d.putCreatureOnBattlefield(opponent, "Test Bear")
        val steal = d.putCardInHand(active, "Test Steal Creature")
        d.giveMana(active, Color.GREEN, 1)
        d.castSpell(active, steal, listOf(stolen))
        d.bothPass()
        d.state.projectedState.getController(stolen) shouldBe active

        d.resolveCreature(active, "Test Any-Player Sacrifice Another")
        d.bothPass() // resolve the ETB trigger

        // The active player is asked and declines; the opponent now controls nothing, so the
        // continuation must skip them rather than offer the Bear they merely own.
        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>().playerId shouldBe active
        d.submitCardSelection(active, emptyList())

        d.pendingDecision shouldBe null
        d.state.getBattlefield() shouldContain stolen // never sacrificed
    }
})
